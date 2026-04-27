#include <jni.h>
#include <string>
#include <vector>
#include <android/log.h>
#include <unistd.h>
#include <sys/mman.h>
#include <fcntl.h>

#define LOG_TAG "XeletronScanner"
#define LOGI(...) __android_log_print(ANDROID_LOG_INFO, LOG_TAG, __VA_ARGS__)

typedef struct {
    uintptr_t start;
    uintptr_t end;
} MemoryRange;

std::vector<MemoryRange> get_executable_ranges(int pid) {
    std::vector<MemoryRange> ranges;
    char line[512];
    char filename[64];
    sprintf(filename, "/proc/%d/maps", pid);
    FILE* fp = fopen(filename, "r");
    if (fp) {
        while (fgets(line, sizeof(line), fp)) {
            if (strstr(line, "r-xp")) {
                uintptr_t start, end;
                sscanf(line, "%lx-%lx", &start, &end);
                ranges.push_back({start, end});
            }
        }
        fclose(fp);
    }
    return ranges;
}

void scan_and_patch(int pid, const uint8_t* pattern, size_t pattern_len, uint32_t patch) {
    auto ranges = get_executable_ranges(pid);
    char mem_path[64];
    sprintf(mem_path, "/proc/%d/mem", pid);
    int fd = open(mem_path, O_RDWR);
    if (fd == -1) return;

    for (const auto& range : ranges) {
        size_t size = range.end - range.start;
        uint8_t* buffer = new uint8_t[size];
        if (pread64(fd, buffer, size, range.start) == size) {
            for (size_t i = 0; i <= size - pattern_len; ++i) {
                if (memcmp(buffer + i, pattern, pattern_len) == 0) {
                    uintptr_t target_addr = range.start + i;
                    pwrite64(fd, &patch, 4, target_addr);
                    LOGI("Xeletron: Pattern found and patched at 0x%lx", target_addr);
                }
            }
        }
        delete[] buffer;
    }
    close(fd);
}

extern "C" JNIEXPORT void JNICALL
Java_com_azoo_vip_core_ProtectionCore_nativeScanAndPatch(JNIEnv* env, jobject, jint pid) {
    LOGI("≈ SirLionV5: /Freedom Executing. Full Memory Scan Engaged.");
    // Example: Pattern for a common heartbeat check
    uint8_t pattern[] = {0xFD, 0x7B, 0xBF, 0xA9, 0xFD, 0x03, 0x00, 0x91};
    uint32_t nop_instr = 0xD503201F; // AArch64 NOP
    scan_and_patch(pid, pattern, sizeof(pattern), nop_instr);
}
