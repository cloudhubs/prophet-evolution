FROM openresty/openresty:trusty

RUN /bin/cp /usr/share/zoneinfo/Asia/Shanghai /etc/localtime && echo 'Asia/Shanghai' >/etc/timezone

COPY ts-ui-dashboard/nginx.conf /usr/local/openresty/nginx/conf/nginx.conf

COPY ts-ui-dashboard/static /usr/share/nginx/html
