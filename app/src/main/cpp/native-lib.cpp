#include <jni.h>
#include <sys/mman.h>
#include <unistd.h>
#include <cstring>

extern "C" JNIEXPORT void JNICALL
Java_com_ata_shield_BypassCore_applyMemoryPatch(JNIEnv* env, jobject /* this */) {
    // Hardcoded offset for heartbeat check, bypass by NOPs
    uintptr_t baseAddr = 0x0;
    uintptr_t targetOffset = 0x123456;

    void* addr = (void*)(baseAddr + targetOffset);
    size_t pageSize = sysconf(_SC_PAGESIZE);
    void* pageStart = (void*)((uintptr_t)addr & ~(pageSize - 1));

    mprotect(pageStart, pageSize, PROT_READ | PROT_WRITE | PROT_EXEC);
    unsigned int nops = 0x90909090;
    memcpy(addr, &nops, sizeof(nops));
    mprotect(pageStart, pageSize, PROT_READ | PROT_EXEC);
}
