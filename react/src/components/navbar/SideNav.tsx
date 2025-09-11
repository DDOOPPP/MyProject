import {NavLink} from "react-router-dom";
import "./NavBar.css"
interface NavItem{
    to: string;
    label: string;
    icon? : unknown;
}

const navItems : NavItem[] = [
    {to: "/", label: "Home"},
    {to: "/setting", label: "Setting"},
]


export default function SideNav({brand = ""}){
    return (
        <div className={"sidenav"}>
            <header className={"sidenav-header"}>{brand}</header>
            <nav className={"sidenav-nav"}>
                {navItems.map(item => (
                    <NavLink className={({isActive}) => `sidenav-link ${isActive ? "active": ""}`} to={item.to}>
                        {item.label}
                    </NavLink>
                ))}
            </nav>
        </div>
    )
}