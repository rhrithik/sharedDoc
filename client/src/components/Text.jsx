import React, { useEffect, useRef, useState } from "react";
import Quill from "quill";
import "quill/dist/quill.snow.css";
import OpenDocument from "./OpenDocument";
import NewDocument from "./NewDocument";
import { useNavigate } from "react-router-dom";
import { jwtDecode } from "jwt-decode";
import ShareDocument from "./ShareDocument";

function Text({
  actionState,
  setActionState,
  documentId,
  setDocumentId,
  access,
  setAccess,
}) {
  const editorRef = useRef(null);
  const quillRef = useRef(null);
  const [content, setContent] = useState("");
  const socketRef = useRef(null);
  const wsUrl = import.meta.env.VITE_WEBSOCKET_URL;
  const token = localStorage.getItem("token");

  const navitage = useNavigate();

  const isTypingRef = useRef(false);
  const typingTimeOutRef = useRef(null);
  const [documentIds, setDocumentIds] = useState([]);
  const [newDoc, setNewDoc] = useState(false);
  const [sharedDocList, setSharedDocList] = useState([]);
  const [newDocumentMessage, setNewDocumentMessage] = useState("");

  const decoded = jwtDecode(token);
  const documentIdRef = useRef(documentId);

  useEffect(() => {
    documentIdRef.current = documentId;
  }, [documentId]);

  useEffect(() => {
    if (!token) navitage("/login");
  }, [token]);

  useEffect(() => {
    if (quillRef.current && !isTypingRef.current) {
      if (quillRef.current.root.innerHTML !== content) {
        quillRef.current.clipboard.dangerouslyPasteHTML(content);
        quillRef.current.setSelection(0, 0);
      }
    }
  }, [content]);

  useEffect(() => {
    if (!token) navitage("/login");
      socketRef.current = new WebSocket(`${wsUrl}?token=${token}`);

      socketRef.current.onopen = () => {
        console.log("Websocket connection opened...");
      };

      socketRef.current.onmessage = (event) => {
        const receivedJSON = JSON.parse(event.data);
        // console.log(receivedJSON);
        if (receivedJSON.action && receivedJSON.action === "documentList") {
          setDocumentIds(receivedJSON.documentIds);
        } else if (
          receivedJSON.action &&
          receivedJSON.action === "sharedDocumentList"
        ) {
          // console.log(receivedJSON);
          setSharedDocList(receivedJSON.documentIds);
        } else if (
          receivedJSON.action &&
          receivedJSON.action === "createDocExists"
        ) {
          setNewDocumentMessage(receivedJSON.message);
          // setActionState("new");
        } else if (
          receivedJSON.action &&
          receivedJSON.action === "documentCreated"
        ) {
          setDocumentId(receivedJSON.message);
          setActionState("edit");
        } else if(receivedJSON.action && receivedJSON.action === "returnDocument") {
          setActionState("edit");
          if (!isTypingRef.current && quillRef.current) {
            if (receivedJSON.documentId === documentIdRef.current) {
              setContent(receivedJSON.message);
            }
            if (receivedJSON.access) setAccess(receivedJSON.access);
            // console.log(receivedJSON)
          }
          // console.log("Event", event.data);
        }
      };

      socketRef.current.onclose = () => {
        console.log("Websocket connection closed...");
      };

      socketRef.current.onerror = (error) => {
        localStorage.removeItem("token");
        navitage("/login"); 
        // console.error("Websocket error: ", error);
      };


    // return ()=>socketRef.current.close();
  }, []);

  useEffect(() => {
    if (quillRef.current) {
      if (access === "READ") {
        quillRef.current.enable(false); 
      } else {
        quillRef.current.enable(true); 
      }
    }
  }, [access]);

  useEffect(() => {
    if (newDoc == true) return;

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
    if (actionState === "open") {
      if (!socketRef.current || socketRef.current.readyState !== 1) return;
      if (socketRef.current && socketRef.current.readyState === 1) {
        const temp = {
          action: "getDocumentList",
        };
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
    } else if (actionState === "share") {
      if (socketRef.current && socketRef.current.readyState === 1) {
        const temp = {
          action: "getSharedDocumentList",
          documentId,
        };
        socketRef.current.send(JSON.stringify(temp));
      }
    }
  }, [documentId, actionState]);

  useEffect(() => {
    if (quillRef.current) {
      quillRef.current.off("text-change");
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

  if (actionState === "new") {
    return (
      <NewDocument
        setActionState={setActionState}
        setDocumentId={setDocumentId}
        socketRef={socketRef}
        newDocumentMessage={newDocumentMessage}
        setNewDocumentMessage={setNewDocumentMessage}
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
        documentId={documentId}
      />
    );
  }
  if (actionState === "share") {
    return (
      <ShareDocument
        setActionState={setActionState}
        socketRef={socketRef}
        documentId={documentId}
        sharedDocList={sharedDocList}
        setSharedDocList={setSharedDocList}
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
