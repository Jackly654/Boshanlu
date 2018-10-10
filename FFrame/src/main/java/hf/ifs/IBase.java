package hf.ifs;

public interface IBase
{
	int S_ENTER_PREV = 1; // 进入前(向左) ←
	int S_ENTER_COMPLETE = 2; // 已经进入当前窗口,并停止滑动(向左) ←
	int S_PAUSE_PREV = 3; // 即将离开(向左) ←
	int S_PAUSE_COMPLETE = 4; // 已经离开,不可见(向左) ←
	int S_RESUME_PREV = 5; // 即将重新回到当前(向右) →
	int S_RESUME_COMPLETE = 6; // 已经回到当前窗口(向右) →
	int S_LEAVE_PREV = 7; // 准备离开(向右) →
	int S_LEAVE_COMPLETE = 8; // 已经离开(向右) →

	int S_PUSH = 10; // 压栈
	int S_PULL = 11; // 弹栈

	void onChangedView(final int status, final Object obj); // 切换界面前后的状态
	boolean onPressBack();
	void onDestroy();
}