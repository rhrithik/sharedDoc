import { useEffect, useState } from "react";

function NewDocument({
  setDocumentId,
  setActionState,
  socketRef,
  newDocumentMessage,
  setNewDocumentMessage
}) {
  // const handleSubmit = () => {
  //   console.log(currId);
  //   setDocumentId(currId);
  //   setActionState("create");
  // };

  useEffect(()=>{
    setNewDocumentMessage("");
  },[])

  const handleSubmit = () => {
    // setDocumentId(currId);
    // setActionState("create");
    if (socketRef.current && socketRef.current.readyState === 1) {
      // console.log(documentId);
      const temp = {
        action: "createDocument",
        documentId:currId,
      };
      // console.log(temp);
      socketRef.current.send(JSON.stringify(temp));
    }
  };

  const [currId, setCurrId] = useState("");

  return (
    <div
      className="h-[89vh] flex justify-center items-center"
      style={{ backgroundColor: "rgba(0, 0, 0, 0.4)" }}
    >
      <div className="bg-white overflow-auto flex flex-col items-center p-5 border rounded-lg border-gray-300 w-[50%] py-10">
        <p className="text-2xl mb-3">Create New Document</p>
        <div className=" flex flex-col items-center w-full mt-5 gap-5">
          <div className="flex flex-col gap-2 items-center w-[100%]">
            <div className="flex flex-row gap-5 w-[100%] justify-center items-center">
              <p className="text-lg">Document Name:</p>
              <input
                type="text"
                value={currId}
                onChange={(e) => setCurrId(e.target.value)}
                className="color-black w-[50%] border border-gray-400 rounded-lg h-10 px-2"
              />
            </div>
            {newDocumentMessage === "" ? null : (
              <p className="text-red-500">{newDocumentMessage}</p>
            )}
          </div>
          <button
            className="bg-[#192952] text-white px-5 py-2 border rounded-lg cursor-pointer"
            onClick={handleSubmit}
          >
            Create
          </button>
        </div>
      </div>
    </div>
  );
}

export default NewDocument;
