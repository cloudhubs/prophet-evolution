FROM mrrm/web.go


RUN mkdir -p /app
WORKDIR /app

ADD ./ts-news-service/src/main/main.go /app/
RUN go build
CMD [ "./app" ]

EXPOSE 12862