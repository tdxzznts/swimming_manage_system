<template>
  <div class="p-2">
    <transition :enter-active-class="proxy?.animate.searchAnimate.enter" :leave-active-class="proxy?.animate.searchAnimate.leave">
      <div v-show="showSearch" class="mb-[10px]">
        <el-card shadow="hover">
          <el-form ref="queryFormRef" :model="queryParams" :inline="true">
            <el-form-item label="权益编码" prop="benefitCode">
              <el-input v-model="queryParams.benefitCode" placeholder="请输入权益编码" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="权益名称" prop="benefitName">
              <el-input v-model="queryParams.benefitName" placeholder="请输入权益名称" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="权益描述" prop="benefitDesc">
              <el-input v-model="queryParams.benefitDesc" placeholder="请输入权益描述" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="权益值" prop="benefitValue">
              <el-input v-model="queryParams.benefitValue" placeholder="请输入权益值" clearable @keyup.enter="handleQuery" />
            </el-form-item>
            <el-form-item label="适用等级" prop="levelValue">
              <el-select v-model="queryParams.levelValue" placeholder="请选择适用等级" clearable>
                <el-option v-for="dict in member_level" :key="dict.value" :label="dict.label" :value="dict.value"/>
              </el-select>
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
            <el-button type="primary" plain icon="Plus" @click="handleAdd" v-hasPermi="['swimming:memberBenefit:add']">新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="success" plain icon="Edit" :disabled="single" @click="handleUpdate()" v-hasPermi="['swimming:memberBenefit:edit']">修改</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="danger" plain icon="Delete" :disabled="multiple" @click="handleDelete()" v-hasPermi="['swimming:memberBenefit:remove']">删除</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button type="warning" plain icon="Download" @click="handleExport" v-hasPermi="['swimming:memberBenefit:export']">导出</el-button>
          </el-col>
          <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
        </el-row>
      </template>

      <el-table v-loading="loading" border :data="memberBenefitList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="主键ID" align="center" prop="id" v-if="false" />
        <el-table-column label="权益编码" align="center" prop="benefitCode" />
        <el-table-column label="权益名称" align="center" prop="benefitName" />
        <el-table-column label="权益描述" align="center" prop="benefitDesc" />
        <el-table-column label="权益值" align="center" prop="benefitValue" />
        <el-table-column label="适用等级" align="center" prop="levelValue" width="120">
          <template #default="scope">
            <dict-tag :options="member_level" :value="scope.row.levelValue"/>
          </template>
        </el-table-column>
        <el-table-column label="状态" align="center" prop="status" width="100">
          <template #default="scope">
            <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
            <el-tag v-else-if="scope.row.status === '1'" type="danger">停用</el-tag>
            <el-tag v-else type="info">{{ scope.row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="备注" align="center" prop="remark" />
        <el-table-column label="操作" align="center" fixed="right"  class-name="small-padding fixed-width">
          <template #default="scope">
            <el-tooltip content="修改" placement="top">
              <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['swimming:memberBenefit:edit']"></el-button>
            </el-tooltip>
            <el-tooltip content="删除" placement="top">
              <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['swimming:memberBenefit:remove']"></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

      <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
    </el-card>
    <!-- 添加或修改会员权益配置对话框 -->
    <el-dialog :title="dialog.title" v-model="dialog.visible" width="500px" append-to-body>
      <el-form ref="memberBenefitFormRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="权益编码" prop="benefitCode">
          <el-input v-model="form.benefitCode" placeholder="请输入权益编码" />
        </el-form-item>
        <el-form-item label="权益名称" prop="benefitName">
          <el-input v-model="form.benefitName" placeholder="请输入权益名称" />
        </el-form-item>
        <el-form-item label="权益描述" prop="benefitDesc">
          <el-input v-model="form.benefitDesc" placeholder="请输入权益描述" />
        </el-form-item>
        <el-form-item label="权益值" prop="benefitValue">
          <el-input v-model="form.benefitValue" placeholder="请输入权益值" />
        </el-form-item>
        <el-form-item label="适用等级" prop="levelValue">
          <el-select v-model="form.levelValue" placeholder="请选择适用等级">
            <el-option
                v-for="dict in member_level"
                :key="dict.value"
                :label="dict.label"
                :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio value="0">正常</el-radio>
            <el-radio value="1">停用</el-radio>
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

<script setup name="MemberBenefit" lang="ts">
import { listMemberBenefit, getMemberBenefit, delMemberBenefit, addMemberBenefit, updateMemberBenefit } from '@/api/swimming/memberBenefit';
import { MemberBenefitVO, MemberBenefitQuery, MemberBenefitForm } from '@/api/swimming/memberBenefit/types';

const { proxy } = getCurrentInstance() as ComponentInternalInstance;
const { member_level } = toRefs<any>(proxy?.useDict('member_level'));

const memberBenefitList = ref<MemberBenefitVO[]>([]);
const buttonLoading = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref<Array<string | number>>([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);

const queryFormRef = ref<ElFormInstance>();
const memberBenefitFormRef = ref<ElFormInstance>();

const dialog = reactive<DialogOption>({
  visible: false,
  title: ''
});

const initFormData: MemberBenefitForm = {
  id: undefined,
  benefitCode: undefined,
  benefitName: undefined,
  benefitDesc: undefined,
  iconName: undefined,
  benefitValue: undefined,
  tagType: undefined,
  levelValue: undefined,
  sortOrder: undefined,
  status: undefined,
  remark: undefined
}
const data = reactive<PageData<MemberBenefitForm, MemberBenefitQuery>>({
  form: {...initFormData},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    benefitCode: undefined,
    benefitName: undefined,
    benefitDesc: undefined,
    iconName: undefined,
    benefitValue: undefined,
    tagType: undefined,
    levelValue: undefined,
    sortOrder: undefined,
    status: undefined,
    params: {
    }
  },
  rules: {
    id: [
      { required: true, message: "主键ID不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询会员权益配置列表 */
const getList = async () => {
  loading.value = true;
  const res = await listMemberBenefit(queryParams.value);
  memberBenefitList.value = res.rows;
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
  memberBenefitFormRef.value?.resetFields();
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
const handleSelectionChange = (selection: MemberBenefitVO[]) => {
  ids.value = selection.map(item => item.id);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
const handleAdd = () => {
  reset();
  dialog.visible = true;
  dialog.title = "添加会员权益配置";
}

/** 修改按钮操作 */
const handleUpdate = async (row?: MemberBenefitVO) => {
  reset();
  const _id = row?.id || ids.value[0]
  const res = await getMemberBenefit(_id);
  Object.assign(form.value, res.data);
  dialog.visible = true;
  dialog.title = "修改会员权益配置";
}

/** 提交按钮 */
const submitForm = () => {
  memberBenefitFormRef.value?.validate(async (valid: boolean) => {
    if (valid) {
      buttonLoading.value = true;
      if (form.value.id) {
        await updateMemberBenefit(form.value).finally(() =>  buttonLoading.value = false);
      } else {
        await addMemberBenefit(form.value).finally(() =>  buttonLoading.value = false);
      }
      proxy?.$modal.msgSuccess("操作成功");
      dialog.visible = false;
      await getList();
    }
  });
}

/** 删除按钮操作 */
const handleDelete = async (row?: MemberBenefitVO) => {
  const _ids = row?.id || ids.value;
  await proxy?.$modal.confirm('是否确认删除会员权益配置编号为"' + _ids + '"的数据项？').finally(() => loading.value = false);
  await delMemberBenefit(_ids);
  proxy?.$modal.msgSuccess("删除成功");
  await getList();
}

/** 导出按钮操作 */
const handleExport = () => {
  proxy?.download('swimming/memberBenefit/export', {
    ...queryParams.value
  }, `memberBenefit_${new Date().getTime()}.xlsx`)
}

onMounted(() => {
  getList();
});
</script>
