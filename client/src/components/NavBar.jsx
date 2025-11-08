import SharedDoc1 from "../assets/SharedDoc1.png";
import login from "../assets/login.png";
import "../App.css";
import { jwtDecode } from "jwt-decode";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
function NavBar({ setActionState, documentId, access, setDocumentId }) {
  const token = localStorage.getItem("token");
  const tokenInfo = jwtDecode(token);
  const navigate = useNavigate();

  const [showDrop, setShowDrop] = useState(false);

  const handleLogout = () => {
    localStorage.removeItem("token");
    navigate("/login");
  };

  return (
    <div className="bg-[#192952]">
      <div className="py-2 flex flex-row justify-between items-center">
        <div className="flex flex-row gap-4">
          <img
            onClick={() => setActionState("open")}
            className="h-[80px] pl-5 cursor-pointer"
            src={SharedDoc1}
            alt=""
          />
          <div className="flex flex-col gap-1 items-start justify-center">
            <div className="flex flex-row items-center justify-center gap-5">
              <p className="text-white text-center text-2xl">
                {documentId}
                {documentId === "" ? "" : ".txt"}
              </p>
              {
                documentId!=""?
                <p className={`rounded px-3 ${(documentId==="" || access==="")?"":access==="OWNER"?'bg-red-200 text-red-600': access==="READ"?'bg-blue-200 text-blue-600 px-3':'bg-green-200 text-green-600 '}`}>{access}</p>
                :
                null
              }
            </div>
            <div className="flex flex-row gap-5">
              <p
                className="text-white cursor-pointer"
                onClick={() => setActionState("new")}
              >
                New
              </p>
              <p
                className="text-white cursor-pointer"
                onClick={() =>{ 
                  setDocumentId("");
                  setActionState("open")
                }}
              >
                Open
              </p>
              {
                (documentId!='' && access==='OWNER')?
                <p className="text-white cursor-pointer" onClick={()=>{
                  setActionState('share')
                }}>Share</p>
                :
                null
              }
            </div>
          </div>
        </div>
        <div className="flex flex-row items-center text-white">
          <p>{tokenInfo.sub}</p>
          <div>
            <img
              className="h-[80px] pr-5 cursor-pointer"
              onClick={() => setShowDrop(!showDrop)}
              src={login}
              alt=""
            />
            <ul className={`absolute ${showDrop ? "" : "hidden"}`}>
              <li
                className="bg-white text-black p-3 rounded border border-gray-300 cursor-pointer"
                onClick={handleLogout}
              >
                Logout
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>
  );
}

export default NavBar;
