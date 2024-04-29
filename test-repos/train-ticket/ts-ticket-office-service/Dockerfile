FROM node

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

RUN mkdir -p /app
WORKDIR /app

ADD ./ts-ticket-office-service/node_modules /app/node_modules
ADD ./ts-ticket-office-service/package.json /app/
ADD ./ts-ticket-office-service/public /app/public
# RUN npm install

ADD ./ts-ticket-office-service/bin /app/bin
ADD ./ts-ticket-office-service/app.js /app/
CMD [ "npm", "start" ]

EXPOSE 16108