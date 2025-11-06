import { useState } from "react";

function NewDocument({ setDocumentId, setActionState }) {
  // const handleSubmit = () => {
  //   console.log(currId);
  //   setDocumentId(currId);
  //   setActionState("create");
  // };

  const handleCheck = async () => {
    try {
      const res = await fetch(
        `${import.meta.env.VITE_API_URL}/api/documents/check`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            action: "checkDocument",
            documentId: currId,
          }),
        }
      );
      if (res.status === "not-found") return;

      setDocumentId(currId);
      setActionState("create");
    } catch (error) {
      console.log(error);
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
          <div className="flex flex-row gap-5 w-[100%] justify-center items-center">
            <p className="text-lg">Document Name:</p>
            <input
              type="text"
              value={currId}
              onChange={(e) => setCurrId(e.target.value)}
              className="color-black w-[50%] border border-gray-400 rounded-lg h-10 px-2"
            />
          </div>
          <button
            className="bg-[#192952] text-white px-5 py-2 border rounded-lg cursor-pointer"
            onClick={handleCheck}
          >
            Create
          </button>
        </div>
      </div>
    </div>
  );
}

export default NewDocument;
