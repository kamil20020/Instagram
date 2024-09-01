package pl.instagram.instagram.model.api.response;

public record UserProfile(
    String id,
    String nickname,
    String firstname,
    String surname,
    String avatar,
    boolean isVerified,
    boolean isPrivate,
    String description,
    Integer followers,
    Integer followings,
    Integer numberOfPosts,
    boolean didLoggedUserFollow
){
    public UserProfile(String id, String nickname, String firstname, String surname, String avatar, boolean isVerified, boolean isPrivate, String description, Integer followers, Integer followings, Integer numberOfPosts) {
        this(id, nickname, firstname, surname, avatar, isVerified, isPrivate, description, followers, followings, numberOfPosts, false);
    }
}
