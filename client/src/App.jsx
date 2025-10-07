import "./App.css";
import Text from "./components/Text";
import NavBar from "./components/NavBar";
import { useEffect, useState } from "react";

function App() {
  
    const [actionState, setActionState] = useState("home");
      const [documentId, setDocumentId] = useState("");

      useEffect(()=>{
        if(documentId===""){
          document.title="SharedDoc"
        }
        else{
          document.title=`${documentId} | SharedDoc`
        }
      },[documentId])

  return (
    <div>
      <NavBar setActionState={setActionState} documentId={documentId} />
      <Text actionState={actionState} setActionState={setActionState} documentId={documentId} setDocumentId={setDocumentId} />
    </div>
)
}

export default App;
