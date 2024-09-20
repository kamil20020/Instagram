# Instagram copy 
This app is mostly a simplified copy of Instagram in terms of its functions and website layout. This project is not done yet, but i am working on it regulary. Project is divided on 2 parts: first part is containing basic functions of instagram e.g. posts and other part is real time chat.

At this moment almost all basic instagram functions are done. I've not started working on chat yet.

## Runing app
Im my opinion the best way to run this app is to use docker-compose.yml file and command `docker-compose up`. To run app this way it is required to have docker installed.

Details:
* In http://localhost:9000/swagger-ui/index.html#/ there is API specificaiton,
* In http://localhost:3000 there is frontend,
* After database launch, backend is creating schema for database by Liquibase and then also by backend test data is loaded,
* On the start mainly 3 accounts are created:

<p align="center">

| **E-mail**                 | **Password** |
|----------------------------|--------------|
| adam@kopia-instagrama.pl   | KamilKamil1% |
| kamil@kopia-instagrama.pl  | KamilKamil1% |
| michał@kopia-instagrama.pl | KamilKamil1% |

<p>

## Functional requirements:
* Register (done),
* Login (done),
* Searching users (done),
* Posts (done),
* Posts' comments (done),
* Rating posts' comments (done),
* Rating posts (done),
* Following (done),
* Stories (skip for now),
* Tags (skip for now),
* Simple chat app (next step).

## Use cases:
<p align="center">
    <img src="project/instagram/inheritance.png">
<p>

<p align="center">
    <img src="project/instagram/user.png">
<p>

<p align="center">
    <img src="project/instagram/unlogged-user.png">
<p>

<p align="center">
    <img src="project/instagram/logged-user.png">
<p>

## Main app's erd diagram:
![Main app's erd diagram](project/instagram/erd.png)

## Technologies:
* Frontend:
    * ReactJS,
    * TypeScript,
    * ReduxJS.
* Backend:
    * Java
    * Spring Boot,
    * Spring Data JPA,
    * Spring Security,
	* Spring Cloud (in the future),
	* Liquibase,
	* REST Assured,
	* Testcontainers.
 * Database - PostgreSQL,
 * General:
    * Communication messages - REST,
	* Architecture - microservices (basic Instagram functions and chat are isolated components)
    * External authentication and authorization provider - Auth0.
 * Deploy:
    * Docker,
    * Docker images repository - Docker Hub,
	* Message broker - Kafka (in the future),
	* CI/CD - GitHub Actions (in the future) i ArgoCD (in the future)
    * Orchestration - Kubernetes,
    * Cloud - Azure AKS (in the future).

## Swagger

<p align="center">
    <img src="swagger/instagram/swagger-1.png">
<p>

<p align="center">
    <img src="swagger/instagram/swagger-2.png">
<p>

<p align="center">
    <img src="swagger/instagram/swagger-3.png">
<p>

## Screen shots

### Menu for unlogged user:
<p align="center">
    <img src="screenshots/instagram/menu-unlogged.png">
<p>

### Register:

#### First register in Auth0:
<p align="center">
    <img src="screenshots/instagram/register.png">
<p>

#### Then fill data in instagram copy app:
<p align="center">
    <img src="screenshots/instagram/fill-data.png">
<p>

### Login by Auth0:
<p align="center">
    <img src="screenshots/instagram/register.png">
<p>

### Menu for logged user:
<p align="center">
    <img src="screenshots/instagram/menu-logged.png">
<p>

### User's own profile:
<p align="center">
    <img src="screenshots/instagram/user-profile-logged.png">
<p>

### Add avatar:

#### Click on current avatar and then select image from file system:
<p align="center">
    <img src="screenshots/instagram/create-avatar.png">
<p>

#### Changed avatar:
<p align="center">
    <img src="screenshots/instagram/user-profile-avatar.png">
<p>

Avatar in the menu will be refreshed after next login;

### Other users' profiles:

#### Profile viewed by unlogged user:
<p align="center">
    <img src="screenshots/instagram/user-profile-unlogged.png">
<p>

#### Profile viewed by logged user:
<p align="center">
    <img src="screenshots/instagram/user-profile-other-logged.png">
<p>

### Searching users:

#### Searching users is possible by clicking on appropriate button in the left menu:
<p align="center">
    <img src="screenshots/instagram/search-users.png">
<p>

#### Search have also lately viewed profiles:
<p align="center">
    <img src="screenshots/instagram/search-users-history.png">
<p>

### Create post:

#### Post creation is possible by selecting appropriate option in left menu:
<p align="center">
    <img src="screenshots/instagram/create-post-1.png">
<p>

#### Then select image from the file system like in the avatar selection and after that fill post description and maybye hide post likes or disable comments:
<p align="center">
    <img src="screenshots/instagram/create-post-2.png">
<p>

#### Accept post creation and then refresh own profile to see new post:
<p align="center">
    <img src="screenshots/instagram/create-post-3.png">
<p>

### Post view:

#### Post viewed by unlogged user:
<p align="center">
    <img src="screenshots/instagram/post-view-unlogged.png">
<p>

#### Post viewed by logged user:
<p align="center">
    <img src="screenshots/instagram/post-view-logged.png">
<p>

#### Post can also be viewied in the separate page and not only in the dialog window:
<p align="center">
    <img src="screenshots/instagram/post-view-page.png">
<p>

#### Post can be deleted by clicking on the dots in the upper right corner:
<p align="center">
    <img src="screenshots/instagram/delete-comment.png">
<p>

### Comments:

#### Comment creation is possible by clicking on the chat icon, which is near to heart icon. Then fill comment content:
<p align="center">
    <img src="screenshots/instagram/create-comment-1.png">
<p>

#### Accept comment creation:
<p align="center">
    <img src="screenshots/instagram/create-comment-2.png">
<p>

#### Comments can have sub comments and their creation is possible by clicking reply button under given comment.
<p align="center">
    <img src="screenshots/instagram/sub-comment.png">
<p>

#### Comment can be deleted by clicking on the options of comment:
<p align="center">
    <img src="screenshots/instagram/delete-comment.png">
<p>

### Post likes:

#### Post like can be added by logged user by clicking on the black outlined heart icon. Heart icon will change border color on red:
<p align="center">
    <img src="screenshots/instagram/like-post.png">
<p>

#### Post likes list can be viewed by clicking on the total post's likes:
<p align="center">
    <img src="screenshots/instagram/post-likes.png">
<p>

### Comment likes:

#### Comment likes can be created by clicking on the hear icon, which is on the right of given comment:
<p align="center">
    <img src="screenshots/instagram/like-comment.png">
<p>

#### Similary like for the post, list of comment's likes can be viewed by clicking on its total number of likes.

### Following:

#### User can follow other user to view his new posts in a convenient way. Following is possible by clicking on the blue follow button in the given user's profile page:
<p align="center">
    <img src="screenshots/instagram/user-profile-other-logged.png">
<p>

<p align="center">
    <img src="screenshots/instagram/follow-user.png">
<p>

#### Then followed user's posts will be viewed in the main page from the left menu:
<p align="center">
    <img src="screenshots/instagram/followed-posts.png">
<p>

#### Lists of followers and followed users can be viewed by clicking on their total amounts in the user's profile:
<p align="center">
    <img src="screenshots/instagram/followers.png">
<p>

### Almost every collections of data that are loaded from api, are requested in portions called pages:
<p align="center">
    <img src="screenshots/instagram/pagination-1.png">
<p>

<p align="center">
    <img src="screenshots/instagram/pagination-2.png">
<p>
