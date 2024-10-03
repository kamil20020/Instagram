FROM node:16.14.0 AS build
WORKDIR /home/app
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /home/app/build /usr/share/nginx/html
COPY --from=build /home/app/nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE $PORT
CMD ["nginx", "-g", "daemon off;"] 
