FROM node:16.14.0 AS build
WORKDIR /frontend
COPY package.json .
RUN npm install
COPY . .
RUN npm run build

FROM nginx:stable-alpine
COPY --from=build /frontend/build /usr/share/nginx/html
COPY --from=build /frontend/nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE $PORT
CMD ["nginx", "-g", "daemon off;"] 