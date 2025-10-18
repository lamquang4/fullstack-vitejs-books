import { BrowserRouter as Router } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import LayoutRoute from "./LayoutRoute";

function App() {
  return (
    <Router>
      <Toaster />
      <LayoutRoute />
    </Router>
  );
}

export default App;
