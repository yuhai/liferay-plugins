/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.wsrp.admin.lar;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.lar.BaseStagedModelDataHandler;
import com.liferay.portal.kernel.lar.ExportImportPathUtil;
import com.liferay.portal.kernel.lar.PortletDataContext;
import com.liferay.portal.kernel.xml.Element;
import com.liferay.portal.model.Group;
import com.liferay.portal.service.GroupLocalServiceUtil;
import com.liferay.portal.service.ServiceContext;
import com.liferay.wsrp.model.WSRPProducer;
import com.liferay.wsrp.service.WSRPProducerLocalServiceUtil;

/**
 * @author Michael C. Han
 */
public class WSRPProducerStagedModelDataHandler
	extends BaseStagedModelDataHandler<WSRPProducer> {

	public static final String[] CLASS_NAMES = {WSRPProducer.class.getName()};

	@Override
	public void deleteStagedModel(
			String uuid, long groupId, String className, String extraData)
		throws PortalException, SystemException {

		Group group = GroupLocalServiceUtil.getGroup(groupId);

		WSRPProducer wsrpProducer =
			WSRPProducerLocalServiceUtil.fetchWSRPProducerByUuidAndCompanyId(
				uuid, group.getCompanyId());

		if (wsrpProducer != null) {
			WSRPProducerLocalServiceUtil.deleteWSRPProducer(wsrpProducer);
		}
	}

	@Override
	public String[] getClassNames() {
		return CLASS_NAMES;
	}

	@Override
	public String getDisplayName(WSRPProducer wsrpProducer) {
		return wsrpProducer.getName();
	}

	@Override
	protected void doExportStagedModel(
			PortletDataContext portletDataContext, WSRPProducer wsrpProducer)
		throws Exception {

		Element wsrpProducerElement = portletDataContext.getExportDataElement(
			wsrpProducer);

		portletDataContext.addClassedModel(
			wsrpProducerElement,
			ExportImportPathUtil.getModelPath(wsrpProducer), wsrpProducer);
	}

	@Override
	protected void doImportStagedModel(
			PortletDataContext portletDataContext, WSRPProducer wsrpProducer)
		throws Exception {

		ServiceContext serviceContext = portletDataContext.createServiceContext(
			wsrpProducer);

		WSRPProducer importedWSRPProducer = null;

		if (portletDataContext.isDataStrategyMirror()) {
			WSRPProducer existingWSRPProducer =
				WSRPProducerLocalServiceUtil.
					fetchWSRPProducerByUuidAndCompanyId(
						wsrpProducer.getUuid(),
						portletDataContext.getCompanyId());

			if (existingWSRPProducer == null) {
				serviceContext.setUuid(wsrpProducer.getUuid());

				importedWSRPProducer =
					WSRPProducerLocalServiceUtil.addWSRPProducer(
						portletDataContext.getUserId(null),
						wsrpProducer.getName(), wsrpProducer.getVersion(),
						wsrpProducer.getPortletIds(), serviceContext);
			}
			else {
				existingWSRPProducer.setName(wsrpProducer.getName());
				existingWSRPProducer.setVersion(wsrpProducer.getVersion());
				existingWSRPProducer.setPortletIds(
					wsrpProducer.getPortletIds());

				WSRPProducerLocalServiceUtil.updateWSRPProducer(
					existingWSRPProducer);

				importedWSRPProducer =
					WSRPProducerLocalServiceUtil.updateWSRPProducer(
						existingWSRPProducer);
			}
		}
		else {
			importedWSRPProducer =
				WSRPProducerLocalServiceUtil.addWSRPProducer(
					portletDataContext.getUserId(null), wsrpProducer.getName(),
					wsrpProducer.getVersion(), wsrpProducer.getPortletIds(),
					serviceContext);
		}

		portletDataContext.importClassedModel(
			wsrpProducer, importedWSRPProducer);
	}

}