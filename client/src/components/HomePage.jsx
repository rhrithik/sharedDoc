import { useEffect } from "react";

function HomePage({ setDocumentId }) {
  useEffect(() => {
    setDocumentId("");
  }, []);

  return (
    <div className="flex justify-center items-center h-[88vh]">
      <p className="text-2xl color-gray-200">
        Create a new Document or Open and Existing Document
      </p>
    </div>
  );
}

export default HomePage;
