const NavMenuItem = (props: { content: any; onClick: () => void }) => {
  return (
    <div className="nav-menu-item" onClick={props.onClick}>
      {props.content}
    </div>
  );
};
export default NavMenuItem;
