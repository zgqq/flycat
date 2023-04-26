#FROM adoptopenjdk/openjdk8-openj9:x86_64-ubuntu-jdk8u232-b09_openj9-0.17.0-slim AS build
FROM amazoncorretto:17 AS build
#RUN yum -y install \
# locales \
# glibc-locale-source \
# glibc-langpack-zh \
# tar && \
# yum -y clean
#RUN localedef -i en_US -f UTF-8 en_US.UTF-8

#RUN install python-pip  && \
# pip install ansi2html

ENV TERM xterm-256color


#RUN apt-get update -y && \
#apt-get -y install \
#make \
#gcc \
#wget \
#tar \
#gzip \
#apr-devel \
#openssl-devel  \
#&& \
#apt-get -y clean && \
#cd /opt &&  \
#wget http://mirrors.tuna.tsinghua.edu.cn/apache/tomcat/tomcat-connectors/native/1.2.23/source/tomcat-native-1.2.23-src.tar.gz && \
#tar -xzvf tomcat-native-1.2.23-src.tar.gz && \
#cd tomcat-native-1.2.23-src/native/ && \
#./configure --with-apr=/usr/bin/apr-1-config \
#            --with-java-home=$JAVA_HOME \
#            --with-ssl=no \
#            --prefix=/usr/local/tomcat/lib/native-jni-lib && \
#make && make install

RUN yum update -y && \
 yum -y install \
wget \
tar  \
gzip \
&& \
 cd /opt &&  \
wget -O bat.zip https://github.com/sharkdp/bat/releases/download/v0.12.1/bat-v0.12.1-x86_64-unknown-linux-musl.tar.gz && \
tar -xvzf bat.zip -C /usr/local && \
rm -f bat.zip && \
cd /usr/local && mv bat-v0.12.1-x86_64-unknown-linux-musl bat

FROM amazoncorretto:17
#FROM adoptopenjdk/openjdk8-openj9:x86_64-ubuntu-jdk8u232-b09_openj9-0.17.0-slim
LABEL maintainer="zgqq <zgqjava@gmail.com>"
#COPY --from=build /usr/local/tomcat/lib/native-jni-lib /usr/local/tomcat/lib/native-jni-lib
COPY --from=build /usr/local/bat /usr/local/bat
#RUN apt-get -y ncurses-devel  \
#geoip-devel  \
#libmaxminddb-devel \
#tokyocabinet-devel  && \
#apt-get -y clean
#RUN cd /opt && \
#wget https://tar.goaccess.io/goaccess-1.3.tar.gz && \
#tar -xzvf goaccess-1.3.tar.gz && \
#rm -rf goaccess-1.3.tar.gz && \
#cd goaccess-1.3/ && \
#./configure --enable-utf8 --enable-geoip=legacy && \
#make && \
#make install && \
#rm -rf goaccess-1.3

COPY --from=hengyunabc/arthas:latest /opt/arthas /opt/arthas

RUN yum update -y && \
 yum -y install \
 curl \
 zsh \
 wget \
 git \
 openssl \
 lsof \
 procps-ng \
 && \
yum clean all && \
yum autoremove -y


# RUN wget https://github.com/robbyrussell/oh-my-zsh/raw/master/tools/install.sh -O - | zsh || true && \
# curl -L git.io/antigen > $HOME/antigen.zsh && \


# 开启zsh
# COPY zshrc /opt/zshrc_
# COPY zsh_history /opt/zsh_history_
# RUN wget https://raw.fastgit.org/robbyrussell/oh-my-zsh/master/tools/install.sh -O - | zsh || true && \
# curl -L https://raw.fastgit.org/zsh-users/antigen/master/bin/antigen.zsh > $HOME/antigen.zsh && \
# cp -f /opt/zshrc_ $HOME/.zshrc && \
# cp -f /opt/zsh_history_ $HOME/.zsh_history && \
# /bin/zsh $HOME/.zshrc


# 开启fzf
# RUN yum update -y && \
#  yum -y install \
#  tar  && \
# git clone --depth 1 https://github.com/junegunn/fzf.git $HOME/.fzf && \
# yes | bash $HOME/.fzf/install && \
# yum clean all && \
# yum autoremove -y

RUN git clone https://github.com/wanhebin/clash-for-linux.git $HOME/clash-for-linux && \
cd $HOME/clash-for-linux

# Copy the entrypoint script
COPY entrypoint.sh /entrypoint.sh
# Set the script as executable
RUN chmod +x /entrypoint.sh
# Set the ENTRYPOINT
ENTRYPOINT ["/entrypoint.sh"]