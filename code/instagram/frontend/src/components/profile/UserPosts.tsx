import Avatar from "../common/Avatar";

const img1 =
  "https://www.imperiumtapet.com/public/uploads/preview/piekne-widoki-7-3315352142308iyuwjrhvf.jpg";
const img2 =
  "https://img.freepik.com/darmowe-zdjecie/gory-vestrahorn-w-stokksnes-na-islandii_335224-667.jpg?w=1380&t=st=1674737578~exp=1674738178~hmac=c701828364cf1333d4ab301db868dabee2a0231cb886bd09dc709852524ae1d9";
const img3 =
  "https://www.galerie-zdjec.pl/wp-content/uploads/2011/02/piekne-widoki-5.jpg";
const img4 = "https://s29.flog.pl/media/foto/13671790_kto-ma-zawsze-pod-gorke-ten-pozniejma-ladne-widoki--.jpg"


const UserPosts = () => {
  return (
    <div
      className="posts"
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(3, 1fr)",
        columnGap: 28,
        rowGap: 28,
      }}
    >
      <div className="post">
        <img alt="Post" src={img1} width={300} height={300} />
      </div>
      <div className="post">
        <img alt="Post" src={img2} width={300} height={300} />
      </div>
      <div className="post">
        <img alt="Post" src={img3} width={300} height={300} />
      </div>
      <div className="post">
        <img alt="Post" src={img4} width={300} height={300} />
      </div>
    </div>
  );
};

export default UserPosts;
