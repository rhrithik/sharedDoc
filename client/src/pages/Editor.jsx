import { useEffect, useState } from "react";
import NavBar from "../components/NavBar";
import Text from "../components/Text";
import { useNavigate } from "react-router-dom";

function Editor() {
  const [actionState, setActionState] = useState("open");
  const [documentId, setDocumentId] = useState("");
  const [loading, setLoading] = useState(true);
    const [access, setAccess] = useState("");
  const navigate = useNavigate();

    const token = localStorage.getItem("token");

  useEffect(() => {
    if(!token){
      navigate('/login');
    }
    setLoading(false);

    if (documentId === "") {
      document.title = "SharedDoc";
    } else {
      document.title = `${documentId} | SharedDoc`;
    }
  }, [documentId]);

    // If no token, don't render anything — just navigate
  if (!token) {
    navigate("/login");
    return null;
  }

  if(loading)return;
  
  return (
    <div>
      <NavBar setActionState={setActionState} documentId={documentId} access={access} />
      <Text
        actionState={actionState}
        setActionState={setActionState}
        documentId={documentId}
        setDocumentId={setDocumentId}
        access={access}
        setAccess={setAccess}
      />
    </div>
  );
}

export default Editor;
