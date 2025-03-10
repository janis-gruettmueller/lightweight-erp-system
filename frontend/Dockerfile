FROM node:18-alpine AS build

WORKDIR /app

COPY frontend/src/package*.json ./

RUN npm install

COPY frontend/src/ ./

RUN npm run build 

FROM nginx:alpine

COPY --from=build /app/build /usr/share/nginx/html

EXPOSE 80 

CMD ["nginx", "-g", "daemon off;"]
