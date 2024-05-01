FROM python:3

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

RUN mkdir -p /app
WORKDIR /app

COPY ts-voucher-service/requirements.txt ./
RUN pip install cryptography
RUN pip install --no-cache-dir -r requirements.txt

ADD ./ts-voucher-service/server.py /app/
CMD [ "python", "server.py" ]

EXPOSE 16101