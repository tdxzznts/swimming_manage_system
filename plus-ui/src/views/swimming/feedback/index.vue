<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="反馈类型" prop="feedbackType">
              <el-select v-model="queryParams.feedbackType" placeholder="请选择反馈类型" clearable >
                <el-option v-for="dict in sp_feedback_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="反馈标题" prop="title">
              <el-input v-model="queryParams.title" placeholder="请输入反馈标题" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="联系方式" prop="contactInfo">
              <el-input v-model="queryParams.contactInfo" placeholder="请输入联系方式" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable >
                <el-option v-for="dict in sp_feedback_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="处理结果" prop="handleResult">
              <el-input v-model="queryParams.handleResult" placeholder="请输入处理结果" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="处理时间" prop="handleTime">
              <el-date-picker clearable
                v-model="queryParams.handleTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择处理时间"
              />
            </el-form-item>
            <el-form-item label="满意度评分" prop="satisfaction">
              <el-input v-model="queryParams.satisfaction" placeholder="请输入满意度评分" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
              <el-button icon="Refresh" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </div>
    </transition>

    <el-card shadow="never">
      <template #header>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:feedback:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:feedback:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:feedback:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:feedback:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="feedbackList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="反馈ID" align="center" prop="id" v-if="false" />
        <el-table-column label="用户名称" align="center" prop="userName" />
        <el-table-column label="反馈类型" align="center" prop="feedbackType">
          <template #default="scope">
            <dict-tag :options="sp_feedback_type" :value="scope.row.feedbackType"/>
          </template>
        </el-table-column>
        <el-table-column label="反馈标题" align="center" prop="title" />
        <el-table-column label="反馈内容" align="center" prop="content" />
        <el-table-column label="联系方式" align="center" prop="contactInfo" />
        <el-table-column label="状态" align="center" prop="status">
          <template #default="scope">
            <dict-tag :options="sp_feedback_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="处理人" align="center" prop="handleUserName" />
        <el-table-column label="处理结果" align="center" prop="handleResult" />
        <el-table-column label="处理时间" align="center" prop="handleTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.handleTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="满意度评分" align="center" prop="satisfaction" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:feedback:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:feedback:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改反馈对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="feedbackFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="反馈类型" prop="feedbackType">
          <el-select v-model="form.feedbackType" placeholder="请选择反馈类型">
            <el-option
                v-for="dict in sp_feedback_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="反馈标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入反馈标题" />
        </el-form-item>
        <el-form-item label="反馈内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="联系方式" prop="contactInfo">
          <el-input v-model="form.contactInfo" placeholder="请输入联系方式" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择状态">
            <el-option
                v-for="dict in sp_feedback_status"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="处理结果" prop="handleResult">
            <el-input v-model="form.handleResult" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="处理时间" prop="handleTime">
          <el-date-picker clearable
            v-model="form.handleTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择处理时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="满意度评分" prop="satisfaction">
          <el-input v-model="form.satisfaction" placeholder="请输入满意度评分" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
            <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button :loading="buttonLoading" type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Feedback" lang="ts">
import { listFeedback, getFeedback, delFeedback, addFeedback, updateFeedback } from '@/api/swimming/feedback';
import { FeedbackVO, FeedbackQuery, FeedbackForm } from '@/api/swimming/feedback/types';
import { useUserStore } from '@/store/modules/user';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_feedback_status, sp_feedback_type } = toRefs<any>(proxy?.useDict('sp_feedback_status', 'sp_feedback_type'));
const userStore = useUserStore();

const feedbackList = ref<FeedbackVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const feedbackFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: FeedbackForm = {
  id: undefined,
  userId: undefined,
  feedbackType: undefined,
  title: undefined,
  content: undefined,
  images: undefined,
  contactInfo: undefined,
  status: undefined,
  handleUserId: undefined,
  handleResult: undefined,
  handleTime: undefined,
  satisfaction: undefined,
  remark: undefined,
}
const data = reactive<PageData<FeedbackForm, FeedbackQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    feedbackType: undefined,
    title: undefined,
    content: undefined,
    images: undefined,
    contactInfo: undefined,
    status: undefined,
    handleUserId: undefined,
    handleResult: undefined,
    handleTime: undefined,
    satisfaction: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "反馈ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    title: [
      { required: true, message: "反馈标题不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "反馈内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询反馈列表 */
const getList = async () => {
  loading.value = true;
  const res = await listFeedback(queryParams.value);
  feedbackList.value = res.rows;
  total.value = res.total;
  loading.value = false;
}

/** 取消按钮 */
const cancel = () => {
  reset();
  dialog.visible = false;
}

/** 表单重置 */
const reset = () => {
  form.value = {...initFormData};
  feedbackFormRef.value?.resetFields();
}

/** 搜索按钮操作 */
const handleQuery = () => {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
const resetQuery = () => {
  queryFormRef.value?.resetFields();
  handleQuery();
}

/** 多选框选中数据 */
const handleSelectionChange = (selection: FeedbackVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加反馈";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: FeedbackVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getFeedback(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改反馈";
}

/** 提交按钮 */
const submitForm = () => {
  feedbackFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        // 修改时，自动设置处理人ID为当前登录用户ID
        form.value.handleUserId = userStore.userId;
        await updateFeedback(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addFeedback(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: FeedbackVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除反馈编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delFeedback(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/feedback/export', {
    ...queryParams.value
  }, `feedback_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
