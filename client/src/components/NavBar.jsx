import SharedDoc1 from "../assets/SharedDoc1.png";
import login from "../assets/login.png";
import "../App.css";
function NavBar({ setActionState, documentId }) {


  return (
    <div className="bg-[#192952]">
      <div className="py-2 flex flex-row justify-between items-center">
        <div className="flex flex-row gap-4">
          <img onClick={()=>setActionState("home")} className="h-[80px] pl-5 cursor-pointer" src={SharedDoc1} alt="" />
          <div className="flex flex-col items-start justify-center">
            <p className="text-white text-2xl">{documentId}{documentId===""?"":".txt"}</p>
            <div className="flex flex-row gap-5">

            <p
              className="text-white cursor-pointer"
              onClick={() => setActionState("new")}
              >
              New
            </p>
            <p
              className="text-white cursor-pointer"
              onClick={() => setActionState("open")}
              >
              Open
            </p>
                </div>
          </div>
        </div>
        <div>
          <img className="h-[80px] pr-5" src={login} alt="" />
        </div>
      </div>
    </div>
  );
}

export default NavBar;
