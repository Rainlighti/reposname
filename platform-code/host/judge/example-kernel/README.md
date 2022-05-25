这个文件夹下的gradelib.py是为了使xv6对接该系统使用的小型库。有一定侵入性，xv6的评测程序中，需要为每个test装饰器添加一个test_point_id参数，指定测试点号码，除此之外不需要做任何改动即可使用xv6自带的实验评测逻辑。

这里就不附带xv6本身了。

对gradelib.py做了修改的部分：
- 在make中加入了编译阶段的接口逻辑（target是空，表示在编译内核，这时才会记录编译中发生的错误，否则视为Judge Error）
- 在run_test加入了测试点的提交、通过、未通过接口逻辑
- 为test装饰器增加了一个参数
- 在每个sys.exit附近加入了judge_error判错逻辑
