import React, { useEffect, useRef, useState } from "react";
import Quill from "quill";
import "quill/dist/quill.snow.css";

function Text() {
  const editorRef = useRef(null);
  const quillRef = useRef(null);
  const [content, setContent] = useState("");
  const socketRef = useRef(null);
  const wsUrl = import.meta.env.VITE_WEBSOCKET_URL;

  const [documentId, setDocumentId] = useState("");
  const isTypingRef = useRef(false);
  const typingTimeOutRef = useRef(null);

  useEffect(() => {
    socketRef.current = new WebSocket(wsUrl);

    socketRef.current.onopen = () => {
      console.log("Websocket connection opened...");    
    };

    socketRef.current.onmessage = (event) => {
      const receivedJSON = JSON.parse(event.data);

      if (!isTypingRef.current &&
        quillRef.current &&
        quillRef.current.root.innerHTML !== receivedJSON.message
      ) {
        quillRef.current.root.innerHTML = receivedJSON.message;

        setDocumentId(receivedJSON.documentId);
      }
      console.log("Event", event.data);
    };


    socketRef.current.onclose = () => {
      console.log("Websocket connection closed...");
    };

    socketRef.current.onerror = (error) => {
      console.error("Websocket error: ", error);
    };


  }, []);

  useEffect(() => {
    const handler = setTimeout(() => {
      if (
        socketRef.current &&
        socketRef.current.readyState === 1 &&
        documentId
      ) {
        const temp = {
          documentId,
          message: content,
        };
        console.log("Sending content: ",JSON.stringify(temp));
        socketRef.current.send(JSON.stringify(temp));
      }
    }, 500);

    return () => {
      clearTimeout(handler);
    };
  }, [content, documentId]);

  useEffect(() => {
    if (editorRef.current && !quillRef.current) {
      quillRef.current = new Quill(editorRef.current, {
        theme: "snow",
        placeholder: "Start typing...",
        modules: {
          toolbar: [
            ["bold", "italic", "underline"],
            [{ header: [1, 2,3, false] }],
            ["link", "blockquote", "code-block"],
            [{ list: "ordered" }, { list: "bullet" }],
          ],
        },
      });

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
  }, []);

  return (
    <div style={{ maxWidth: "700px", margin: "40px auto", color: "black" }}>
      <h2>Shared Doc</h2>
      <div ref={editorRef} style={{ height: "300px", marginBottom: "20px" }} />
    </div>
  );
}

export default Text;
