FROM node:18-alpine AS build

WORKDIR /app
COPY src/package*.json ./
RUN npm ci

COPY src/ ./
RUN npm run build

FROM node:18-alpine

WORKDIR /app
COPY --from=build /app ./
RUN npm ci --omit=dev

EXPOSE 3000

CMD ["npm", "start"]