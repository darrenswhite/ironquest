FROM node:14-alpine AS deps

# Check https://github.com/nodejs/docker-node/tree/b4117f9333da4138b03a546ec926ef50a31506c3#nodealpine to understand why libc6-compat might be needed.
RUN apk add --no-cache libc6-compat

WORKDIR /ironquest

COPY package.json package-lock.json ./
RUN npm ci

FROM node:14-alpine AS proddeps

WORKDIR /ironquest

COPY package.json package-lock.json ./
RUN npm ci --only=production

FROM node:14-alpine AS builder

WORKDIR /ironquest

COPY . .
COPY --from=deps /ironquest/node_modules ./node_modules
ARG API
RUN npm run build

FROM node:14-alpine AS runner

WORKDIR /ironquest

COPY --from=builder /ironquest/build ./build
COPY --from=proddeps /ironquest/node_modules ./node_modules
COPY --from=builder /ironquest/serve.js ./
COPY --from=builder /ironquest/package.json ./

EXPOSE 8081

CMD [ "npm", "start" ]
