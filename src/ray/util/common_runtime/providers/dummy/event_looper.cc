# include "event_looper.h"


CrEventLoop *CrCreateEventLoop(int setsize)
{
    return nullptr;
}

void CrDeleteEventLoop(CrEventLoop *eventLoop)
{

}

void CrStop(CrEventLoop *eventLoop)
{

}

int CrCreateFileEvent(CrEventLoop *eventLoop, int fd, int mask,
        CrFileProc *proc, void *clientData)
{
    return 0;
}

void CrDeleteFileEvent(CrEventLoop *eventLoop, int fd, int mask)
{

}

int CrGetFileEvents(CrEventLoop *eventLoop, int fd)
{
    return 0;
}

long long CrCreateTimeEvent(CrEventLoop *eventLoop, long long milliseconds,
        CrTimeProc *proc, void *clientData)
{
    return 0;
}

int CrDeleteTimeEvent(CrEventLoop *eventLoop, long long id)
{
    return 0;
}

void CrMain(CrEventLoop *eventLoop)
{

}

const char *CrGetApiName(void)
{
    return "dummy";
}

int CrGetSetSize(CrEventLoop *eventLoop)
{
    return 0;
}

int CrResizeSetSize(CrEventLoop *eventLoop, int setsize)
{
    return 0;
}