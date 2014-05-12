#include <string.h>
#include <jni.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <netinet/in.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
extern "C" {
JNIEXPORT jstring JNICALL
Java_com_ul_upnpbl_LightActivity_recvFromJNI
(JNIEnv *env, jobject obj, jstring jip)
{
	const char *ip = env->GetStringUTFChars(jip, NULL) ;
	struct sockaddr_in localSock;
	struct ip_mreq group;
	int sd;
	int datalen;
	int outPort = 0;
	char databuf[1024] { 0 };
	char ipStrArr[16] = { 0 };
	char *ipStr = ipStrArr;
	char resp[1050] = { 0 }; //all plus 2 ; as delimiters plus 8 for port
	struct sockaddr_in remaddr; /* remote address */

	if(ip == (const char *)0 ) {
		return env->NewStringUTF("err");
	}

	socklen_t addrlen = sizeof(remaddr); /* length of addresses */
	sd = socket(AF_INET, SOCK_DGRAM, 0);

	if(sd < 0)
	{
		env->ReleaseStringUTFChars(jip, ip);
		return env->NewStringUTF("err");
	}
	else
	printf("Opening datagram socket....OK.\n");
	/* Enable SO_REUSEADDR to allow multiple instances of this */
	/* application to receive copies of the multicast datagrams. */
	{
		int reuse = 1;
		if(setsockopt(sd, SOL_SOCKET, SO_REUSEADDR, (char *)&reuse, sizeof(reuse)) < 0)
		{
			perror("Setting SO_REUSEADDR error");
			close(sd);
			env->ReleaseStringUTFChars(jip, ip);
			return env->NewStringUTF("err");
		}
		else
		printf("Setting SO_REUSEADDR...OK.\n");
	}

	/* Bind to the proper port number with the IP address */

	/* specified as INADDR_ANY. */

	memset((char *) &localSock, 0, sizeof(localSock));
	localSock.sin_family = AF_INET;
	localSock.sin_port = htons(1900);
	localSock.sin_addr.s_addr = INADDR_ANY;
	if(bind(sd, (struct sockaddr*)&localSock, sizeof(localSock)))
	{
		perror("Binding datagram socket error");
		close(sd);
		env->ReleaseStringUTFChars(jip, ip);
		return env->NewStringUTF("err");
	}
	else
	printf("Binding datagram socket...OK.\n");

	group.imr_multiaddr.s_addr = inet_addr("239.255.255.250");
	group.imr_interface.s_addr = inet_addr(ip);
	env->ReleaseStringUTFChars(jip, ip);
	if(setsockopt(sd, IPPROTO_IP, IP_ADD_MEMBERSHIP, (char *)&group, sizeof(group)) < 0)
	{
		perror("Adding multicast group error");
		int err = errno;
		close(sd);
		return env->NewStringUTF("err");

	}

	else

	printf("Adding multicast group...OK.\n");

	/* Read from the socket. */

	datalen = sizeof(databuf);
	if(recvfrom(sd, databuf, datalen, 0, (struct sockaddr *)&remaddr, &addrlen) < 0)
	{
		perror("Reading datagram message error");
		close(sd);
		return env->NewStringUTF("err");
	}
	else
	{
		printf("Reading datagram message...OK.\n");
		printf("The message from multicast server is: \"%s\"\n", databuf);
	}
	ipStr = inet_ntoa(remaddr.sin_addr);
	outPort = ntohs(remaddr.sin_port);
	sprintf(resp, "%s;%d;%s", ipStr, outPort, databuf);

	return env->NewStringUTF((const char *) resp); //yes, yes I know it is c style cast :D
}
}
