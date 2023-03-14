import Avatar from "../common/Avatar";

const UserStories = () => {
  return (
    <div className="stories" style={{ display: "flex" }}>
      <div className="story">
        <Avatar image={undefined} width={128} height={128} />
        <h4>Story 1</h4>
      </div>
      <div className="story">
        <Avatar image={undefined} width={128} height={128} />
        <h4>Story 2</h4>
      </div>
      <div className="story">
        <Avatar image={undefined} width={128} height={128} />
        <h4>Story 3</h4>
      </div>
      <div className="story">
        <Avatar image={undefined} width={128} height={128} />
        <h4>Story 4</h4>
      </div>
    </div>
  );
};

export default UserStories;
