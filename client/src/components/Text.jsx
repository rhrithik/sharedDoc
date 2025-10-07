import React, {useEffect, useRef, useState } from "react";
import Quill from "quill";
import "quill/dist/quill.snow.css";
import OpenDocument from "./OpenDocument";
import NewDocument from "./NewDocument";
import HomePage from "./HomePage";

function Text({ actionState, setActionState, documentId, setDocumentId }) {
  const editorRef = useRef(null);
  const quillRef = useRef(null);
  const [content, setContent] = useState("");
  const socketRef = useRef(null);
  const wsUrl = import.meta.env.VITE_WEBSOCKET_URL;

  const isTypingRef = useRef(false);
  const typingTimeOutRef = useRef(null);
  const [documentIds, setDocumentIds] = useState([]);

  const [newDoc, setNewDoc] = useState(false);

  useEffect(() => {
    if (quillRef.current && !isTypingRef.current) {
      if (quillRef.current.root.innerHTML !== content) {
        quillRef.current.clipboard.dangerouslyPasteHTML(content);
        quillRef.current.setSelection(0,0);
      }
    }
  }, [content]);



  useEffect(() => {
    socketRef.current = new WebSocket(wsUrl);

    socketRef.current.onopen = () => {
      console.log("Websocket connection opened...");
    };

    socketRef.current.onmessage = (event) => {
      const receivedJSON = JSON.parse(event.data);
      // console.log(receivedJSON);
      if (receivedJSON.action && receivedJSON.action === "documentList") {
        setDocumentIds(receivedJSON.documentIds);
      } 
      else {
        setActionState("edit");
        if (!isTypingRef.current && quillRef.current) {
          setContent(receivedJSON.message);
        }
        // console.log("Event", event.data);
      }
    };

    socketRef.current.onclose = () => {
      console.log("Websocket connection closed...");
    };

    socketRef.current.onerror = (error) => {
      console.error("Websocket error: ", error);
    };

    // return ()=>socketRef.current.close();
  }, []);


  useEffect(() => {
    if(newDoc==true)return;

    const handler = setTimeout(() => {
      if (
        socketRef.current &&
        socketRef.current.readyState === 1 &&
        documentId
      ) {
        const temp = {
          action: actionState,
          documentId,
          message: content,
        };
        // console.log("Sending content: ", JSON.stringify(temp));
        socketRef.current.send(JSON.stringify(temp));
      }
    }, 500);

    return () => {
      clearTimeout(handler);
    };
  }, [content]);

  useEffect(() => {
    if(actionState==="open"){
      if(socketRef.current && socketRef.current.readyState===1){
        const temp={
          action:"getDocumentList"
        }
        socketRef.current.send(JSON.stringify(temp));
      }
    }
    if (actionState === "edit") {
      if (socketRef.current && socketRef.current.readyState === 1) {
        // console.log(documentId);
        const temp = {
          action: "getDocument",
          documentId,
        };
        // console.log(temp);
        socketRef.current.send(JSON.stringify(temp));
      }
    } else if (actionState === "create") {
      if (socketRef.current && socketRef.current.readyState === 1) {
        // console.log(documentId);
        const temp = {
          action: "createDocument",
          documentId,
        };
        // console.log(temp);
        socketRef.current.send(JSON.stringify(temp));
      }
    }
  }, [documentId,actionState]);

  useEffect(() => {

  if (quillRef.current) {
    quillRef.current.off('text-change');
    quillRef.current = null;
    if (editorRef.current) {
      editorRef.current.innerHTML = "";
    }
  }

  if (editorRef.current) {
    setNewDoc(true);
    quillRef.current = new Quill(editorRef.current, {
      theme: "snow",
      placeholder: "Start typing...",
      modules: {
        toolbar: [
          ["bold", "italic", "underline"],
          [{ header: [1, 2, 3, false] }],
          ["link", "blockquote", "code-block"],
          [{ list: "ordered" }, { list: "bullet" }],
        ],
      },
    });

    if (content) {
      quillRef.current.clipboard.dangerouslyPasteHTML(content);
      setTimeout(() => {
        try {
          const length = quillRef.current.getLength();
          quillRef.current.setSelection(length - 1, 0);
        } catch (e) {
          console.warn("setSelection error: ", e);
        }
      }, 100);
    }

    quillRef.current.on("text-change", (delta, oldDelta, source) => {
      if (source === "user") {
        isTypingRef.current = true;
        setContent(quillRef.current.root.innerHTML);

        if (typingTimeOutRef.current) clearTimeout(typingTimeOutRef.current);

        typingTimeOutRef.current = setTimeout(() => {
          isTypingRef.current = false;
        }, 1500);
      }
    });
  }

  setNewDoc(false);
}, [actionState, documentId]);




  if (actionState === "home") {
    return <HomePage setDocumentId={setDocumentId} />;
  }
  if (actionState === "new") {
    return (
      <NewDocument
        setActionState={setActionState}
        setDocumentId={setDocumentId}
      />
    );
  }
  if (actionState === "open") {
    return (
      <OpenDocument
      socketRef={socketRef}
        documentIds={documentIds}
        setActionState={setActionState}
        setDocumentId={setDocumentId}
      />
    );
  }

  if (actionState === "edit") {
    return (
      <div className="">
        <div className="px-5 py-5 color-black bg-white">
          <div ref={editorRef} className="h-[70vh]" />
        </div>
      </div>
    );
  }
}

export default Text;