import './App.css'
import {BrowserRouter, Route, Routes} from "react-router-dom";
import SideNav from "./components/navbar/SideNav.tsx";
import "./style/base.css";
import SettingPage from "./page/SettingPage.tsx";
function App() {

  return (
      <BrowserRouter>
          <div className="app-layout">
              <main className="app-content">
                  <SideNav brand="Test Nav" />
                  <Routes>
                      <Route path="/" element={<p>Test</p>} />
                      <Route path="/setting" element={<SettingPage/>}/>
                  </Routes>
              </main>
          </div>
      </BrowserRouter>
  )
}

export default App
