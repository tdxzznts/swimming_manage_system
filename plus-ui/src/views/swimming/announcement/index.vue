<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="公告标题" prop="title">
              <el-input v-model="queryParams.title" placeholder="请输入公告标题" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="公告类型" prop="announcementType">
              <el-select v-model="queryParams.announcementType" placeholder="请选择公告类型" clearable >
                <el-option v-for="dict in sp_announcement_type" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="状态" prop="status">
              <el-select v-model="queryParams.status" placeholder="请选择状态" clearable >
                <el-option v-for="dict in announcement_status" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
            </el-form-item>
            <el-form-item label="发布时间" prop="publishTime">
              <el-date-picker clearable
                v-model="queryParams.publishTime"
                type="date"
                value-format="YYYY-MM-DD"
                placeholder="请选择发布时间"
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:announcement:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:announcement:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:announcement:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:announcement:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="announcementList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="公告ID" align="center" prop="id" v-if="false" />
        <el-table-column label="公告标题" align="center" prop="title" />
        <el-table-column label="公告类型" align="center" prop="announcementType">
          <template #default="scope">
            <dict-tag :options="sp_announcement_type" :value="scope.row.announcementType"/>
          </template>
        </el-table-column>
        <el-table-column label="发布时间" align="center" prop="publishTime" width="180">
          <template #default="scope">
            <span>{{ parseTime(scope.row.publishTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <dict-tag :options="announcement_status" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="浏览次数" align="center" prop="viewCount" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:announcement:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:announcement:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改系统公告对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="announcementFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="公告标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="公告内容">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
        <el-form-item label="公告类型" prop="announcementType">
          <el-select v-model="form.announcementType" placeholder="请选择公告类型">
            <el-option
                v-for="dict in sp_announcement_type"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="发布时间" prop="publishTime">
          <el-date-picker clearable
            v-model="form.publishTime"
            type="datetime"
            value-format="YYYY-MM-DD HH:mm:ss"
            placeholder="请选择发布时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">草稿</el-radio>
            <el-radio value="1">已发布</el-radio>
          </el-radio-group>
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

<script setup name="Announcement" lang="ts">
import { listAnnouncement, getAnnouncement, delAnnouncement, addAnnouncement, updateAnnouncement } from '@/api/swimming/announcement';
import { AnnouncementVO, AnnouncementQuery, AnnouncementForm } from '@/api/swimming/announcement/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { sp_announcement_type, announcement_status } = toRefs<any>(proxy?.useDict('sp_announcement_type', 'announcement_status'));

const announcementList = ref<AnnouncementVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const announcementFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: AnnouncementForm = {
  id: undefined,
  title: undefined,
  content: undefined,
  announcementType: undefined,
  publishTime: undefined,
  status: undefined,
  viewCount: undefined,
  remark: undefined,
}
const data = reactive<PageData<AnnouncementForm, AnnouncementQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: undefined,
    content: undefined,
    announcementType: undefined,
    publishTime: undefined,
    status: undefined,
    viewCount: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "公告ID不能为空", trigger: "blur" }
    ],
    title: [
      { required: true, message: "公告标题不能为空", trigger: "blur" }
    ],
    content: [
      { required: true, message: "公告内容不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询系统公告列表 */
const getList = async () => {
  loading.value = true;
  const res = await listAnnouncement(queryParams.value);
  announcementList.value = res.rows;
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
  announcementFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: AnnouncementVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加系统公告";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: AnnouncementVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getAnnouncement(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改系统公告";
}

/** 提交按钮 */
const submitForm = () => {
  announcementFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateAnnouncement(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addAnnouncement(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: AnnouncementVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除系统公告编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delAnnouncement(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/announcement/export', {
    ...queryParams.value
  }, `announcement_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
