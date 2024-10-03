# Nginx reverse proxy for Nginx Ingress Controller

## Build image
```
docker build -t nginx-reverse-proxy .
```

## Run locally container:
```
docker run --name nginx-reverse-proxy nginx-reverse-proxy -p 80:80
```

## Tag img:
```
docker tag nginx-reverse-proxy kamil20/nginx-reverse-proxy
```

## Push image:
```
docker push kamil20/nginx-reverse-proxy
```
