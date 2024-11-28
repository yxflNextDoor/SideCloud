package com.side.framework.core.tools;

import com.google.common.collect.Lists;
import com.side.framework.core.entity.TreeBasic;


import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 树型组织工具类
 *
 * @author yxfl
 * @date 2023/12/21 22
 **/
@SuppressWarnings("all")
public class TreeHelper {

    /**
     * 默认偏移量
     */
    private static Integer DEFAULT_OFFSET = 0;

    /**
     * 默认起始索引
     */
    private static Integer DEFAULT_BEGIN_INDEX = 1;

    private TreeHelper() {
    }

    /**
     * 扁平化树型组织
     *
     * @param originalTreeList 树型组织列表
     * @return 扁平化后的树型组织列表
     */
    public static List<TreeBasic> flatToList(List<TreeBasic> originalTreeList) {
        return extractTreeToList(originalTreeList, Lists.newArrayList());
    }

    /**
     * 将树中所有的节点都搜集到collectList中
     *
     * @param originalTreeList
     * @param collectList
     * @return
     */
    private static List<TreeBasic> extractTreeToList(List<TreeBasic> originalTreeList, List<TreeBasic> collectList) {
        if (originalTreeList == null || originalTreeList.size() == 0) {
            return collectList;
        }
        collectList.addAll(originalTreeList);
        List<TreeBasic> children = originalTreeList.stream()
                .flatMap(treeBasic -> {
                    List<TreeBasic> childrenList = treeBasic.getChildrenList();
                    return childrenList == null ? Stream.empty() : childrenList.stream();
                }).collect(Collectors.toList());
        return extractTreeToList(children, collectList);
    }

    /**
     * 将原始树的集合转换成树
     * 非递归方式
     *
     * @param originalTreeList 原始树的集合
     * @return
     */
    public static List<TreeBasic> buildTree(List<TreeBasic> originalTreeList) {
        //清空父子关系
        originalTreeList.forEach(treeBasic -> treeBasic.setChildrenList(null));
        Map<Long, List<TreeBasic>> pMap = originalTreeList.stream().collect(Collectors.groupingBy(TreeBasic::getParentId, Collectors.toList()));
        //每一个节点完成子节点的设置
        originalTreeList.forEach(treeBasic -> {
            if (pMap.containsKey(treeBasic.getId())) {
                treeBasic.setChildrenList(pMap.get(treeBasic.getParentId()));
            }
        });
        //所有节点的id集合
        Set<Long> idSet = originalTreeList.stream().map(treeBasic -> treeBasic.getId()).collect(Collectors.toSet());
        //获取根节点集合（过滤点非空节点）
        List<TreeBasic> outs = originalTreeList.stream().filter(treeBasic -> !idSet.contains(treeBasic.getParentId())).collect(Collectors.toList());
        return outs;
    }

    /**
     * 将原始树的集合深拷贝并转换成树
     *
     * @param originalTreeList 原始树的集合
     * @return
     */
    public static List<TreeBasic> cloneAndBuildTree(List<TreeBasic> originalTreeList) {
        if (Objects.isNull(originalTreeList)) {
            throw new IllegalArgumentException("The collection cannot be empty!");
        }
        List<TreeBasic> cloneTreeList = originalTreeList
                .stream()
                .map(treeBasic -> {
                    Object obj = JsonHelper.jsonToObj(JsonHelper.objToJson(treeBasic), TreeBasic.class);
                    return (TreeBasic) obj;
                })
                .collect(Collectors.toList());
        return buildTree(cloneTreeList);
    }

    /**
     * 根据level获取集合
     *
     * @param originalTreeList
     * @return
     */
    public static List<TreeBasic> getByLevel(List<TreeBasic> originalTreeList, Integer level) {
        if (Objects.isNull(originalTreeList)) {
            throw new IllegalArgumentException("The collection cannot be empty!");
        }
        return originalTreeList.stream().filter(treeBasic -> Objects.equals(treeBasic.getLevel(), level)).collect(Collectors.toList());
    }

    /**
     * 刷新左右索引
     *
     * @param originalTreeList 原始树的集合
     * @param index 默认起始的索引
     * @return
     */
    public static List<TreeBasic> refreshLeftAndRight(List<TreeBasic> originalTreeList, Integer index) {
        if (Objects.isNull(index)) {
            return refreshLeftAndRight(originalTreeList, DEFAULT_BEGIN_INDEX);
        }
        for (TreeBasic basic : originalTreeList) {
            basic.setLeftIndex(index);
            index++;
            if (basic.getChildrenList() != null && basic.getChildrenList().size() > 0) {
                refreshLeftAndRight(basic.getChildrenList(), index);
            }
            basic.setRightIndex(index);
            index++;
        }
        return originalTreeList;
    }

    /**
     * 校验ids集合 默认ids按顺序校验
     *
     * @param originalTreeList
     * @param ids
     * @return
     */
    public static Boolean checkChainById(List<TreeBasic> originalTreeList, List<Long> ids) {
        return checkChain(originalTreeList, new TreeSet<>(ids), true, true, DEFAULT_OFFSET);
    }

    /**
     * 校验ids集合
     *
     * @param originalTreeList 原始树的集合,已经构造成树
     * @param ids              待校验的集合
     * @param isScatter        是否离散校验 true:不连续（离散：ids在树之间不相连） false:连续（连续：ids在树之间相连）
     * @param isSequence       是否顺序校验 true:ids本身是顺序的，从树的根节点开始，false:ids本身不是顺序的
     * @param offset           偏移量，用于连续校验时，指定从树的第几个节点开始校验，默认从根节点开始校验
     * @return
     */
    private static Boolean checkChain(List<TreeBasic> originalTreeList, TreeSet<Long> ids, Boolean isScatter, Boolean isSequence, Integer offset) {
        if (originalTreeList == null || ids == null) {
            throw new IllegalArgumentException("The collection cannot be empty!");
        }

        //防止泛型实际为LinedHashMap这种特殊情况（OpenFeign中会转换成该类型,但泛型检查会通过）
        if (!(originalTreeList.stream().findAny().orElse(new TreeBasic()) instanceof TreeBasic)) {
            List<TreeBasic> basics = flatToList(originalTreeList);
            List<TreeBasic> newTrees = basics.stream().map(basic -> MapHelper.convertToPojo((Map<String, Object>) basic, TreeBasic.class)).collect(Collectors.toList());
            originalTreeList = buildTree(newTrees);
        }

        if (Objects.isNull(isSequence)) {
            isSequence = Boolean.TRUE;
        }
        if (Objects.isNull(isScatter)) {
            isScatter = Boolean.TRUE;
        }
        if (Objects.isNull(offset)) {
            offset = DEFAULT_OFFSET;
        }

        //ids为空说明校验通过
        if (ids.size() == 0) {
            return Boolean.TRUE;
        }
        //原始树为空且递归的ids还有剩余元素，说明校验不通过
        if (originalTreeList.size() == 0 && ids.size() > 0) {
            return Boolean.FALSE;
        }

        //ids本身带顺序
        if (isSequence) {
            for (TreeBasic basic : originalTreeList) {
                if (basic.getId().equals(ids.first())) {
                    offset++;
                    return checkChain(Objects.isNull(basic.getChildrenList()) ? Collections.emptyList() : basic.getChildrenList(),
                            ids, isScatter, isSequence, offset);
                }
            }
            return firstMissCallBack(originalTreeList, ids, isScatter, isSequence, offset);
        } else {
            //ids本身无顺序
            for (TreeBasic basic : originalTreeList) {
                if (ids.contains(basic.getId())) {
                    offset++;
                    ids.remove(basic.getId());
                    return checkChain(Objects.isNull(basic.getChildrenList()) ? Collections.emptyList() : basic.getChildrenList(),
                            ids, isScatter, isSequence, offset);
                }
            }
            return firstMissCallBack(originalTreeList, ids, isScatter, isSequence, offset);

        }
    }

    /**
     * 第一层元素没有命中，递归查找
     *
     * @param originalTreeList 原始树的集合,已经构造成树
     * @param ids              待校验的集合
     * @param isScatter        是否离散校验 true:不连续（离散：ids在树之间不相连） false:连续（连续：ids在树之间相连）
     * @param isSequence       是否顺序校验 true:ids本身是顺序的，从树的根节点开始，false:ids本身不是顺序的
     * @param offset           偏移量，用于连续校验时，指定从树的第几个节点开始校验，默认从根节点开始校验
     * @return
     */
    private static Boolean firstMissCallBack(List<TreeBasic> originalTreeList, TreeSet<Long> ids, Boolean isScatter, Boolean isSequence, Integer offset) {
        List<TreeBasic> subList = originalTreeList.stream()
                .flatMap(treeBasic -> {
                    List<TreeBasic> childrenList = treeBasic.getChildrenList();
                    return (childrenList != null) ? childrenList.stream() : Stream.empty();
                }).collect(Collectors.toList());
        if (isScatter) {
            return checkChain(subList, ids, isScatter, isSequence, offset);
        } else {
            //数据本身连续
            if (Objects.equals(offset, DEFAULT_OFFSET)) {
                //已经发生了偏移，但是树的第一层没有找到，说明校验不通过
                return Boolean.FALSE;
            } else {
                return checkChain(subList, ids, isScatter, isSequence, offset);
            }
        }
    }
}
