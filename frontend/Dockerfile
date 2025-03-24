FROM node:18-alpine AS build

WORKDIR /app
COPY src/package*.json ./
RUN npm install

COPY src/ ./
RUN npm run build

FROM node:18-alpine

WORKDIR /app
COPY --from=build /app ./
RUN npm install --omit=dev  # Ensure production dependencies only

EXPOSE 3000

CMD ["npm", "start"]
