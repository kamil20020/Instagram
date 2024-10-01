## 1
FROM node:16.14.0 AS build
WORKDIR /frontend
COPY package.json .
RUN npm install

ARG REACT_APP_API
ARG REACT_APP_CHAT_URL

ENV REACT_APP_API=${REACT_APP_API}
ENV REACT_APP_CHAT_URL=${REACT_APP_CHAT_URL}

COPY . .
RUN npm run build

## 2
FROM nginx:stable-alpine
COPY --from=build /frontend/build /usr/share/nginx/html
COPY --from=build /frontend/nginx/nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE $PORT
CMD ["nginx", "-g", "daemon off;"] 