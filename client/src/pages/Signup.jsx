import { useNavigate } from "react-router-dom";
import SharedDoc1 from "../assets/SharedDoc1.png";
import BG from "../assets/bg.jpg";
import axios from "axios";
import { useState } from "react";

function Signup() {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [showPass, setShowPass] = useState(false);
  const navigate = useNavigate();

  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      const res = await axios.post(`${import.meta.env.VITE_API_URL}/auth/signup`, {
        username,
        password,
      });
      localStorage.setItem("token", res.data.token);
      navigate("/editor");
    } catch (err) {
      alert("Signup failed");
    }
  };

  return (
    <div
      className="flex justify-center items-center h-[100vh] bg-center bg-cover"
      style={{ backgroundImage: `url(${BG})` }}
    >
      <div className="flex flex-col bg-white gap-5 border border-gray-200 h-[70%] w-[50%] items-center justify-center rounded-xl px-5">
        <div className="h-[30%] ">
          <img src={SharedDoc1} className="h-[100%]" alt="" />
        </div>
        <form
          className="flex flex-col gap-5 w-[100%] lg:w-[50%]"
          onSubmit={handleSignup}
        >
          <div>
            <p className="text-3xl text-center text-blue-900 font-mono mb-10">
              Create Account
            </p>
            <p className="text-sm sm:text-md md:text-lg">Username</p>
            <input
              type="text"
              className="border border-gray-200 w-[100%] px-2 py-1 text-sm sm:text-md md:text-lg rounded-lg"
              onChange={(e) => setUsername(e.target.value)}
            />
            <p className="text-sm sm:text-md md:text-lg">Password</p>
            <div className="relative">
              <input
                type={showPass ? "text" : "password"}
                className="border border-gray-200 w-[100%] px-2 py-1 text-sm sm:text-md md:text-lg rounded-lg"
                onChange={(e) => setPassword(e.target.value)}
              />
              <button type="button" onClick={()=>setShowPass(!showPass)} className="absolute inset-y-0 right-0 flex items-center pr-3 text-gray-500 hover:text-gray-700 cursor-pointer">{showPass ? "Hide" : "Show"}</button>
            </div>
          </div>
          <button
            type="submit"
            className="bg-blue-500 text-white px-3 py-1 rounded-sm cursor-pointer text-center"
          >
            Sign Up
          </button>
        </form>
        <button
          className="text-blue-500 underline cursor-pointer"
          onClick={() => navigate("/login")}
        >
          Already have an account? Login
        </button>
      </div>
    </div>
  );
}

export default Signup;
