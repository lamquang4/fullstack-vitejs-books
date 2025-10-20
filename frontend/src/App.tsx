import { BrowserRouter as Router } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import LayoutRoute from "./LayoutRoute";
import ScrollToTop from "./components/ScrollToTop";

function App() {
  return (
    <Router>
      <Toaster />
      <ScrollToTop />
      <LayoutRoute />
    </Router>
  );
}

export default App;
