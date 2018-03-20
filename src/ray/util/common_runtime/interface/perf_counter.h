# pragma once 

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
