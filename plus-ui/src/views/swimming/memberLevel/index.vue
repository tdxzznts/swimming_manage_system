<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="等级编码" prop="levelCode">
              <el-input v-model="queryParams.levelCode" placeholder="请输入等级编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级名称" prop="levelName">
              <el-input v-model="queryParams.levelName" placeholder="请输入等级名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="英文名称" prop="levelEn">
              <el-input v-model="queryParams.levelEn" placeholder="请输入英文名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级值" prop="levelValue">
              <el-input-number v-model="queryParams.levelValue" :min="0" :max="99" placeholder="请输入等级值" clearable />
            </el-form-item>
            <el-form-item label="升级阈值" prop="price">
              <el-input v-model="queryParams.price" placeholder="请输入升级阈值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="等级描述" prop="description">
              <el-input v-model="queryParams.description" placeholder="请输入等级描述" clearable @keyup.enter="handleQuery" />
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:memberLevel:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:memberLevel:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:memberLevel:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:memberLevel:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="memberLevelList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="false" />
        <el-table-column label="等级编码" align="center" prop="levelCode" />
        <el-table-column label="等级名称" align="center" prop="levelName" />
        <el-table-column label="英文名称" align="center" prop="levelEn" />
        <el-table-column label="等级值" align="center" prop="levelValue" />
        <el-table-column label="升级阈值" align="center" prop="price" />
        <el-table-column label="等级描述" align="center" prop="description" />
        <el-table-column label="状态" align="center" prop="status" />
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:memberLevel:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:memberLevel:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员等级配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberLevelFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="等级编码" prop="levelCode">
          <el-input v-model="form.levelCode" placeholder="请输入等级编码" />
        </el-form-item>
        <el-form-item label="等级名称" prop="levelName">
          <el-input v-model="form.levelName" placeholder="请输入等级名称" />
        </el-form-item>
        <el-form-item label="英文名称" prop="levelEn">
          <el-input v-model="form.levelEn" placeholder="请输入英文名称" />
        </el-form-item>
        <el-form-item label="等级值" prop="levelValue">
          <el-input-number v-model="form.levelValue" :min="0" :max="99" placeholder="请输入等级值" />
        </el-form-item>
        <el-form-item label="升级阈值" prop="price">
          <el-input v-model="form.price" placeholder="请输入升级阈值" />
        </el-form-item>
        <el-form-item label="等级描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入等级描述" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input v-model="form.sortOrder" placeholder="请输入排序" />
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

<script setup name="MemberLevel" lang="ts">
import { listMemberLevel, getMemberLevel, delMemberLevel, addMemberLevel, updateMemberLevel } from '@/api/swimming/memberLevel';
import { MemberLevelVO, MemberLevelQuery, MemberLevelForm } from '@/api/swimming/memberLevel/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;

const memberLevelList = ref<MemberLevelVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberLevelFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberLevelForm = {
  id: undefined,
  levelCode: undefined,
  levelName: undefined,
  levelEn: undefined,
  levelValue: undefined,
  discount: undefined,
  price: undefined,
  cardColorStart: undefined,
  cardColorEnd: undefined,
  description: undefined,
  status: undefined,
  sortOrder: undefined,
  remark: undefined
}
const data = reactive<PageData<MemberLevelForm, MemberLevelQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    levelCode: undefined,
    levelName: undefined,
    levelEn: undefined,
    levelValue: undefined,
    discount: undefined,
    price: undefined,
    cardColorStart: undefined,
    cardColorEnd: undefined,
    description: undefined,
    status: undefined,
    sortOrder: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
    levelCode: [
      { required: true, message: "等级编码(normal/silver/gold/diamond)不能为空", trigger: "blur" }
    ],
    levelName: [
      { required: true, message: "等级名称不能为空", trigger: "blur" }
    ],
    levelValue: [
      { required: true, message: "等级值(0-3)不能为空", trigger: "change" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员等级配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberLevel(queryParams.value);
  memberLevelList.value = res.rows;
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
  memberLevelFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberLevelVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员等级配置";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberLevelVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberLevel(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员等级配置";
}

/** 提交按钮 */
const submitForm = () => {
  memberLevelFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberLevel(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberLevel(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberLevelVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员等级配置编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberLevel(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/memberLevel/export', {
    ...queryParams.value
  }, `memberLevel_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
