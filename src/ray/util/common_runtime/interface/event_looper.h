#ifndef RAY_UTIL_COMMON_RUNTIME_INTERFACE_EVENT_LOOPER_H
#define RAY_UTIL_COMMON_RUNTIME_INTERFACE_EVENT_LOOPER_H

/// error code
#define CR_OK 0
#define CR_ERR -1

/// mask
#define CR_NONE 0
#define CR_READABLE 1
#define CR_WRITABLE 2

/// events
#define CR_FILE_EVENTS 1
#define CR_TIME_EVENTS 2
#define CR_ALL_EVENTS (CR_FILE_EVENTS|CR_TIME_EVENTS)
#define CR_DONT_WAIT 4

/// timer return
#define CR_NOMORE -1
#define CR_DELETED_EVENT_ID -1

struct CrEventLoop;

/* callback types */
typedef void CrFileProc(CrEventLoop *eventLoop, int fd, void *clientData, int mask);
typedef int CrTimeProc(CrEventLoop *eventLoop, long long id, void *clientData);

/* Prototypes */
CrEventLoop *CrCreateEventLoop(int setsize);
void CrDeleteEventLoop(CrEventLoop *eventLoop);
void CrStop(CrEventLoop *eventLoop);
int CrCreateFileEvent(CrEventLoop *eventLoop, int fd, int mask,
        CrFileProc *proc, void *clientData);
void CrDeleteFileEvent(CrEventLoop *eventLoop, int fd, int mask);
int CrGetFileEvents(CrEventLoop *eventLoop, int fd);
long long CrCreateTimeEvent(CrEventLoop *eventLoop, long long milliseconds,
        CrTimeProc *proc, void *clientData);
int CrDeleteTimeEvent(CrEventLoop *eventLoop, long long id);
void CrMain(CrEventLoop *eventLoop);
const char *CrGetApiName(void);
int CrGetSetSize(CrEventLoop *eventLoop);
int CrResizeSetSize(CrEventLoop *eventLoop, int setsize);

#endif  // RAY_UTIL_COMMON_RUNTIME_INTERFACE_EVENT_LOOPER_H