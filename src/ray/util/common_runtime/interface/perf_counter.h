#ifndef RAY_UTIL_COMMON_RUNTIME_INTERFACE_PERF_COUNTER_H
#define RAY_UTIL_COMMON_RUNTIME_INTERFACE_PERF_COUNTER_H

typedef enum CrPerfCounterType
{
    COUNTER_TYPE_NUMBER,
    COUNTER_TYPE_RATE,
    COUNTER_TYPE_NUMBER_PERCENTILES,
    COUNTER_TYPE_INVALID,
    COUNTER_TYPE_COUNT
} CrPerfCounterType;

class CrPerfCounter
{
public:
    CrPerfCounter(const char* section, const char *name, CrPerfCounterType type, const char *dsptr);
    ~CrPerfCounter(void);

    // make sure they are called after init above
    void   increment();
    void   decrement();
    void   add(unsigned long long val);
    void   set(unsigned long long val);
    double get_value();

private:
    void* impl_;
};

#endif  // RAY_UTIL_COMMON_RUNTIME_INTERFACE_PERF_COUNTER_H