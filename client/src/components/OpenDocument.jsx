import { useEffect, useState } from "react";

function OpenDocument({socketRef, documentIds, setDocumentId, setActionState }) {

  if (!documentIds || !Array.isArray(documentIds)) {
    return <div>Loading document list...</div>;
  }

  const [docs,setDocs] = useState(documentIds);

  const handleSelection=(id)=>{
    setDocumentId(id);
    setActionState("edit");
  }

  const handleDelete =(id)=>{
    const confirmed = window.confirm(`Do you want to delete this file: ${id}?`);
    if(!confirmed)return;

    const temp = {
      action:"deleteDocument",
      documentId:id
    }
    socketRef.current.send(JSON.stringify(temp));
    setDocs(docs.filter(el=>el!==id));
  }

  
  return (
    <div className="h-[89vh] flex justify-center items-center" style={{ backgroundColor: 'rgba(0, 0, 0, 0.4)' }}>
      <div className="bg-white h-[70%] w-[70%] overflow-auto flex flex-col items-center p-5">
        <p className="text-xl">Open Existing Document</p>
        <div className=" flex flex-col items-start w-full mt-5 gap-2">

        {docs.map((id) => (
            <div key={id}  className="flex justify-between items-center border border-gray-300 rounded-s w-[100%] py-5 h-12 cursor-pointer hover:bg-gray-50"> 
                <p onClick={()=>handleSelection(id)} className="color-black w-[100%] mr-10 px-10 py-2">{id}</p>
                <p onClick={()=>handleDelete(id)} className="text-white bg-red-400 hover:bg-red-200 mx-10 rounded-s px-2 py-1">delete</p>
            </div>
        ))}
        </div>
      </div>
    </div>
  );
}

export default OpenDocument;
