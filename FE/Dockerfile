FROM node:14.17.0
RUN npm install -g serve

RUN mkdir ./build
ADD ./build ./build

ENTRYPOINT ["serve", "-s", "build"]