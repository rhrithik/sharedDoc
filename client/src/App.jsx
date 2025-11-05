import "./App.css";
import { Navigate, Route, Routes } from "react-router-dom";
import Login from "./pages/Login";
import Editor from "./pages/Editor";
import Signup from "./pages/Signup";

function App() {
  const token = localStorage.getItem("token");

  return (
      <Routes>
        <Route path="/login" element={<Login />} />
        <Route path="/signup" element={<Signup />} />
        <Route
          path="/editor"
          element={<Editor />}
        />
        <Route path="*" element={<Navigate to="/login" />} />
      </Routes>
  
  );
}

export default App;
