import { useState } from "react";
import { useNavigate } from "react-router-dom";

function ShareDocument({
  socketRef,
  documentId,
  sharedDocList,
  setSharedDocList,
  setActionState
}) {
  const [username, setUsername] = useState();
  const [newAccess, setNewAccess] = useState();

  const handleUpdate = (id, access) => {
    let temp = {
      action: "updateAccess",
      documentId,
      username: id,
      access,
    };
    socketRef.current.send(JSON.stringify(temp));

    if (socketRef.current && socketRef.current.readyState === 1) {
      temp = {
        action: "getSharedDocumentList",
        documentId,
      };
      socketRef.current.send(JSON.stringify(temp));
    }
  };

  const handleDelete = (id) => {
    const confirm = window.confirm(`Are you sure you want to remove ${id}?`);
    if (!confirm) return;
    handleUpdate(id, "delete");
  };

  return (
    <div
      className="h-[89vh] flex justify-center items-center"
      style={{ backgroundColor: "rgba(0, 0, 0, 0.4)" }}
    >
      <div className="bg-white h-[70%] w-[70%] overflow-auto flex flex-col items-center p-5">
        <div className="relative flex items-center w-full">
          <button className="absolute left-0 text-xl px-3 bg-blue-500 text-white rounded-full pb-1 cursor-pointer" onClick={()=>setActionState("edit")} >&larr;</button>
          <p className="text-xl font-semibold mx-auto">Share Document</p>
        </div>
        <div className=" flex flex-col items-start w-full mt-5 gap-2 ">
          <div className="flex flex-row gap-5">
            <div className="flex flex-row gap-2">
              <p>Add New User: </p>
              <input
                type="text"
                className="border border-gray-500 rounded text-black px-2"
                placeholder="Username"
                onChange={(e) => setUsername(e.target.value)}
              />
              <select
                // onChange={(e) => handleUpdate(id[0], e.target.value)}
                className="cursor-pointer border border-gray-500 rounded px-2"
                onChange={(e) => setNewAccess(e.target.value)}
              >
                <option className="cursor-pointer" value="OWNER">
                  Owner
                </option>
                <option className="cursor-pointer" value="READ">
                  Read
                </option>
                <option className="cursor-pointer" value="WRITE">
                  Write
                </option>
              </select>
            </div>
            <button
              onClick={() => {
                handleUpdate(username, newAccess);
              }}
              className="bg-blue-500 text-white px-3 py-1 rounded cursor-pointer font-mono text-sm"
            >
              Add
            </button>
          </div>
          {sharedDocList.map((id) => {
            return (
              <div
                key={id[0]}
                className="flex justify-between items-center border border-gray-300 rounded-s w-[100%] py-5 h-12 cursor-pointer hover:bg-gray-50"
              >
                <div className="flex flex-row w-[100%] items-center gap-5 mx-2 justify-between">
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
                    // onClick={() => handleSelection(id[0])}
                    className="color-black w-[100%] font-semibold py-2"
                  >
                    {id[0]}
                  </p>
                  <select
                    defaultValue={id[1]}
                    onChange={(e) => handleUpdate(id[0], e.target.value)}
                    className="cursor-pointer"
                  >
                    <option className="cursor-pointer" value="OWNER">
                      Owner
                    </option>
                    <option className="cursor-pointer" value="READ">
                      Read
                    </option>
                    <option className="cursor-pointer" value="WRITE">
                      Write
                    </option>
                  </select>
                  <button
                    className="bg-red-300 text-red-700 px-2 text-center rounded py-1 cursor-pointer"
                    onClick={() => handleDelete(id[0])}
                  >
                    remove
                  </button>
                </div>
                {/* {id[1] === "OWNER" ? (
                  <p
                    onClick={() => handleDelete(id[0])}
                    className="text-white bg-red-400 hover:bg-red-200 mx-2 rounded-sm px-2 py-1"
                  >
                    delete
                  </p>
                ) : null} */}
              </div>
            );
          })}
        </div>
      </div>
    </div>
  );
}

export default ShareDocument;
