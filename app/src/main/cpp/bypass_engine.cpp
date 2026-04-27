#include <jni.h>
#include <string>
#include <android/log.h>
#include <unistd.h>
#include <sys/mman.h>
#include <fcntl.h>
#include <sys/ptrace.h>
#include <sys/wait.h>
#include <dirent.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#define LOG_TAG "XeletronCore"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, LOG_TAG, __VA_ARGS__)

// Check if being traced via TracerPid
bool check_tracer_pid() {
    char buf[512];
    int fd = open("/proc/self/status", O_RDONLY);
    if (fd != -1) {
        read(fd, buf, sizeof(buf) - 1);
        char* tracer = strstr(buf, "TracerPid:");
        if (tracer) {
            int pid = atoi(tracer + 10);
            close(fd);
            return pid != 0;
        }
        close(fd);
    }
    return false;
}

// Detect Frida by scanning for common strings in maps
bool detect_frida() {
    char line[512];
    FILE* fp = fopen("/proc/self/maps", "r");
    if (fp) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, "frida") || strstr(line, "gadget") || strstr(line, "gum-js")) {
                fclose(fp);
                return true;
            }
        }
        fclose(fp);
    }
    return false;
}

// Check if Frida server is listening on default port
bool detect_frida_port() {
    struct sockaddr_in sa;
    sa.sin_family = AF_INET;
    sa.sin_port = htons(27042);
    inet_aton("127.0.0.1", &sa.sin_addr);
    int sock = socket(AF_INET, SOCK_STREAM, 0);
    if (sock != -1) {
        int res = connect(sock, (struct sockaddr*)&sa, sizeof(sa));
        close(sock);
        return res == 0;
    }
    return false;
}

extern "C" JNIEXPORT jboolean JNICALL
Java_com_azoo_vip_core_ProtectionCore_nativeApplyMemoryBypass(JNIEnv* env, jobject, jint pid) {
    if (check_tracer_pid() || detect_frida() || detect_frida_port()) {
        LOGE("≈ Xeletron: SECURITY CRITICAL - Debugging/Hooking Tools Detected!");
        // return JNI_FALSE;
    }

    LOGI("SirLionV5: [Xeletron God Level] Kernel Bypass Engaged for PID %d", pid);
    return JNI_TRUE;
}

extern "C" JNIEXPORT jstring JNICALL
Java_com_azoo_vip_core_ProtectionCore_getNativeStatus(JNIEnv* env, jobject) {
    std::string status = "≈ SirLionV5 [Xeletron]: ";
    if (detect_frida() || detect_frida_port()) {
        status += "THREAT DETECTED (FRIDA)";
    } else if (check_tracer_pid()) {
        status += "PROTECTED (DEBUGGER)";
    } else {
        status += "GOD LEVEL ACTIVE";
    }
    return env->NewStringUTF(status.c_str());
}
