import { useEffect, useState } from "react";

function OpenDocument({
  socketRef,
  documentIds,
  setDocumentId,
  setActionState,
}) {
  const [docs, setDocs] = useState(documentIds);

  useEffect(() => {
    setDocs(documentIds || []);
  }, [documentIds]);

  if (!documentIds || !Array.isArray(documentIds)) {
    return <div>Loading document list...</div>;
  }

  const handleSelection = (id) => {
    setDocumentId(id);
    setActionState("edit");
  };

  const handleDelete = (id) => {
    const confirmed = window.confirm(`Do you want to delete this file: ${id}?`);
    if (!confirmed) return;

    const temp = {
      action: "deleteDocument",
      documentId: id,
    };
    socketRef.current.send(JSON.stringify(temp));
    setDocs((prevDocs) => prevDocs.filter((el) => el[0] !== id));
  };

  return (
    <div
      className="h-[89vh] flex justify-center items-center"
      style={{ backgroundColor: "rgba(0, 0, 0, 0.4)" }}
    >
      <div className="bg-white h-[70%] w-[70%] overflow-auto flex flex-col items-center p-5">
        <p className="text-xl">Open Existing Document</p>
        <div className=" flex flex-col items-center w-full mt-5 gap-2 ">
          {docs.length === 0 ? (
            <div className="">
              <p>
                There are no documents. Create a new document to get started.
              </p>
            </div>
          ) : (
            docs.map((id) => (
              <div
                key={id[0]}
                className="flex justify-between items-center border border-gray-300 rounded-s w-[100%] py-5 h-12 cursor-pointer hover:bg-gray-50"
              >
                <div className="flex flex-row items-center gap-5 mx-2">
                  <p
                    className={`rounded  px-2 py-1 ${
                      id[1] === "OWNER"
                        ? "bg-red-200 text-red-600"
                        : id[1] === "READ"
                        ? "bg-blue-200 text-blue-600 px-3"
                        : "bg-green-200 text-green-600 "
                    }`}
                  >
                    {id[1]}
                  </p>
                  <p
                    onClick={() => handleSelection(id[0])}
                    className="color-black w-[100%] font-semibold py-2"
                  >
                    {id[0]}
                  </p>
                </div>
                {id[1] === "OWNER" ? (
                  <p
                    onClick={() => handleDelete(id[0])}
                    className="text-white bg-red-400 hover:bg-red-200 mx-2 rounded-sm px-2 py-1"
                  >
                    delete
                  </p>
                ) : null}
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}

export default OpenDocument;
