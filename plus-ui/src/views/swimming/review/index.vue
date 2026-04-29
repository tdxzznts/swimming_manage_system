<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="用户ID" prop="userId">
              <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="预约ID" prop="reservationId">
              <el-input v-model="queryParams.reservationId" placeholder="请输入预约ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="评分" prop="rating">
              <el-input v-model="queryParams.rating" placeholder="请输入评分" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="管理员回复" prop="reply">
              <el-input v-model="queryParams.reply" placeholder="请输入管理员回复" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="回复人ID" prop="replyUserId">
              <el-input v-model="queryParams.replyUserId" placeholder="请输入回复人ID" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="回复时间" prop="replyTime">
              <el-date-picker clearable
                v-model="queryParams.replyTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择回复时间"
              />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:review:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:review:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:review:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:review:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="reviewList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="评价ID" align="center" prop="id" v-if="true" />
        <el-table-column label="用户ID" align="center" prop="userId" />
        <el-table-column label="预约ID" align="center" prop="reservationId" />
        <el-table-column label="评分" align="center" prop="rating" />
        <el-table-column label="评价内容" align="center" prop="content" />
        <el-table-column label="图片URL" align="center" prop="images" />
        <el-table-column label="状态" align="center" prop="status" />
        <el-table-column label="管理员回复" align="center" prop="reply" />
        <el-table-column label="回复人ID" align="center" prop="replyUserId" />
        <el-table-column label="回复时间" align="center" prop="replyTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.replyTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:review:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:review:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改评价对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="reviewFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="预约ID" prop="reservationId">
          <el-input v-model="form.reservationId" placeholder="请输入预约ID" />
        </el-form-item>
        <el-form-item label="评分" prop="rating">
          <el-input v-model="form.rating" placeholder="请输入评分" />
        </el-form-item>
        <el-form-item label="评价内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="管理员回复" prop="reply">
            <el-input v-model="form.reply" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="回复人ID" prop="replyUserId">
          <el-input v-model="form.replyUserId" placeholder="请输入回复人ID" />
        </el-form-item>
        <el-form-item label="回复时间" prop="replyTime">
          <el-date-picker clearable
            v-model="form.replyTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择回复时间">
          </el-date-picker>
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

<script setup name="Review" lang="ts">
import { listReview, getReview, delReview, addReview, updateReview } from '@/api/swimming/review';
import { ReviewVO, ReviewQuery, ReviewForm } from '@/api/swimming/review/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const reviewList = ref<ReviewVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const reviewFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: ReviewForm = {
  id: undefined,
  userId: undefined,
  reservationId: undefined,
  rating: undefined,
  content: undefined,
  images: undefined,
  status: undefined,
  reply: undefined,
  replyUserId: undefined,
  replyTime: undefined,
  remark: undefined,
}
const data = reactive<PageData<ReviewForm, ReviewQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    userId: undefined,
    reservationId: undefined,
    rating: undefined,
    content: undefined,
    images: undefined,
    status: undefined,
    reply: undefined,
    replyUserId: undefined,
    replyTime: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "评价ID不能为空", trigger: "blur" }
    ],
    userId: [
      { required: true, message: "用户ID不能为空", trigger: "blur" }
    ],
    rating: [
      { required: true, message: "评分不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询评价列表 */
const getList = async () => {
  loading.value = true;
  const res = await listReview(queryParams.value);
  reviewList.value = res.rows;
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
  reviewFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: ReviewVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加评价";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: ReviewVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getReview(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改评价";
}

/** 提交按钮 */
const submitForm = () => {
  reviewFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateReview(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addReview(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: ReviewVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除评价编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delReview(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/review/export', {
    ...queryParams.value
  }, `review_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
