#ifndef RAY_UTIL_COMMON_RUNTIME_INTERFACE_LOGGING_H
#define RAY_UTIL_COMMON_RUNTIME_INTERFACE_LOGGING_H

# include <iostream>
# include <type_traits>

# ifndef _WIN32
# include <execinfo.h>
# endif

class CrLogger {
 public:
  CrLogger(int severity);
  virtual ~CrLogger();

  template <class T>
  CrLogger &operator<<(const T &t) {
      //static_assert (false, "not implemented");
      return *this;
  }

  // TODO: implement specialization for common types:
  //   integers/float/double/bool/char*/std::string/map/list/set...

 protected:
  // implemented by different providers
  void write_string(const char* str, size_t sz);

 protected:
  const int severity_;
  bool has_logged_;
};

class CrFatalLogger : public CrLogger {
 public:
  CrFatalLogger(int severity) : CrLogger(severity) {}
  virtual ~CrFatalLogger() 
  { 
    if (has_logged_) {
#if defined(_EXECINFO_H) || !defined(_WIN32)
      void *buffer[255];
      const int calls = backtrace(buffer, sizeof(buffer) / sizeof(void *));
      backtrace_symbols_fd(buffer, calls, 1);
#endif
    }
    std::abort();
  }
};

#endif  // RAY_UTIL_COMMON_RUNTIME_INTERFACE_LOGGING_H