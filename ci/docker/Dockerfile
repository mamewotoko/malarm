#base is https://github.com/samtstern/android-vagrant/tree/master/docker/android-sdk

FROM java:jdk

ENV DEBIAN_FRONTEND noninteractive

# Dependencies
RUN dpkg --add-architecture i386 && apt-get update && apt-get install -yq libstdc++6:i386 zlib1g:i386 libncurses5:i386 ant maven --no-install-recommends
ENV GRADLE_URL http://services.gradle.org/distributions/gradle-2.2.1-all.zip
RUN curl -L ${GRADLE_URL} -o /tmp/gradle-2.2.1-all.zip && unzip /tmp/gradle-2.2.1-all.zip -d /usr/local && rm /tmp/gradle-2.2.1-all.zip
ENV GRADLE_HOME /usr/local/gradle-2.2.1

# Download and untar SDK
ENV ANDROID_SDK_URL http://dl.google.com/android/android-sdk_r24.0.2-linux.tgz
RUN curl -L ${ANDROID_SDK_URL} | tar xz -C /usr/local
ENV ANDROID_HOME /usr/local/android-sdk-linux

# Install Android SDK components
#ENV ANDROID_SDK_COMPONENTS platform-tools,build-tools-21.1.2,android-21,extra-android-support

#ENV ANDROID_SDK_COMPONENTS android-25,android-24,tools,platform-tools,build-tools-25.1.0,build-tools-24.0.3,extra-android-support
#RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --all --filter "${ANDROID_SDK_COMPONENTS}"

RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --filter android-10,android-16,android-24,android-25 # > /dev/null
RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --filter tools,platform-tools 
RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --filter build-tools-25.1.0
RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --filter build-tools-24.0.3 
RUN echo y | ${ANDROID_HOME}/tools/android update sdk --no-ui --filter extra-android-m2repository

# Path
ENV PATH $PATH:${ANDROID_HOME}/tools:$ANDROID_HOME/platform-tools:${GRADLE_HOME}/bin

RUN mkdir /proj
WORKDIR /proj

