import { NavLink } from "react-router-dom";

const NavLinkItem = (props: { link: string; content: any }) => {
  return (
    <NavLink
      className={({ isActive }) =>
        isActive ? "nav-item nav-item-active" : "nav-item"
      }
      to={props.link}
    >
      <h1>{props.content}</h1>
    </NavLink>
  );
};

export default NavLinkItem;
