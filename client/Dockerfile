FROM node:12-alpine

WORKDIR /home/ironquest

COPY package*.json ./

RUN npm install

COPY . ./

RUN npm run compile

RUN mkdir /tmp/ironquest && \
    mv build package*.json serve.js /tmp/ironquest && \
    rm -rf ./* && \
    mv /tmp/ironquest/* . && \
    rm -rf /tmp/ironquest

RUN npm install --only=production

CMD [ "npm", "start" ]
